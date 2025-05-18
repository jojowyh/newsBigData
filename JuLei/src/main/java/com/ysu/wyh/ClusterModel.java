package com.ysu.wyh;

import com.google.common.reflect.TypeToken;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.shade.com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.ysu.wyh.SimilarityCalculator.cosineSimilarity;

public class ClusterModel {

    //上下文
    Context context;
    //相似度计算器
    SimilarityCalculator similarityCalculator;
    //用于查询：质心的数量的key
    String num = "numOfZhiXin";
    private static final int INIT_BATCH_SIZE = 50; // 初始数据积累窗口(积累50条再开始分簇)
    private Queue<Map<String, Double>> containerToBatchInit = new LinkedList<>();
    //质心列表
    List<Map<String, Double>> initCentroid = new ArrayList<>();

    // 积累到一定数量后再更新质心
    private Integer BATCH_TO_UPDATE = 50;
    private Map<Integer, List<Map<String, Double>>> containerToBatchUpdate = new ConcurrentHashMap<>();

    //积累到一定数量后再生成新簇
    private Integer BATCH_TO_CREATE = 50;
    private Map<Integer, Queue<Map<String,Double>>> containerToBatchCreate = new ConcurrentHashMap<>();

    // 在ClusterModel类中定义缓存容器
    private List<Map<String, Double>> vectorCache = new ArrayList<>();

    public ClusterModel(Context context) {
        this.context = context;
        this.similarityCalculator = new SimilarityCalculator();
    }

    public int predict(Map<String, Double> vector, Context context) {
        // 将当前处理向量存入缓存
        vectorCache.add(vector);

        // 定期清理旧数据（滑动窗口机制）
        if(vectorCache.size() > 500) { // 保留最近400个向量
            vectorCache = vectorCache.subList(90, 490);
        }

        // 没有质心的时候，先积累一些数据计算初始质心
        if (context.getCounter(num) == 0) {
            containerToBatchInit.add(vector);
            //积累50条时，先分出三簇来（整出三个质心）
            if (containerToBatchInit.size() >= INIT_BATCH_SIZE) {
                initializeCentroids(context); // 触发初始化
                containerToBatchInit.clear();
            }
            return -1; // 表示还没分簇
        }
        // ...后续预测逻辑
        double maxSim = 0;
        int targetCluster = -1;

        // 遍历所有质心
        for (int i=1; i<=context.getCounter(num); i++) {
            double sim = similarityCalculator.cosineDistance(vector, deserialize(context.getState("centroid_" + i)));
            if (sim > maxSim) {
                maxSim = sim;
                targetCluster = i;
            }
        }

        // 动态创建新簇条件，相似度小于阈值就创建新簇
        double threshold = calculateThreshold(vectorCache);
        if (maxSim < threshold) { // 相似度阈值可配置
            containerToBatchCreate.computeIfAbsent(-1, k -> new LinkedList<>()).add(vector);
            if(containerToBatchCreate.get(-1).size() >= BATCH_TO_CREATE) {
                Map<String,Double> newCentroid = calculateCentroid(containerToBatchCreate.get(-1));
                createNewCluster(newCentroid);
                containerToBatchCreate.remove(-1);
            }
            return -1; // 暂不分配
        }
        return targetCluster;
    }

    /**
     * 积累到一定数量后分三个簇出来
     * @param context
     */
    private void initializeCentroids(Context context) {
        // 使用K-means++思想选择初始质心[3,6](@ref)
        List<Map<String, Double>> samples = new ArrayList<>(containerToBatchInit);

        // 随机选择第一个质心
        initCentroid.add(samples.remove(ThreadLocalRandom.current().nextInt(samples.size())));

        // 迭代选择后续质心
        while (initCentroid.size() < 3) { // 初始建议3个质心
            List<Double> distances = samples.stream()
                    .map(vec -> similarityCalculator.minDistance(vec, initCentroid))
                    .collect(Collectors.toList());
            int selected = similarityCalculator.weightedRandomSelect(distances);
            initCentroid.add(samples.remove(selected));
        }

        // 存储到上下文状态
        for (int i=0; i<initCentroid.size(); i++) {
            createNewCluster(initCentroid.get(i));
        }
    }

    /**
     * 创建新质心/簇：
     * counter+1
     * 存到上下文：
     * @param newCentroid
     */
    private void createNewCluster(Map<String,Double> newCentroid) {
        long oldId = context.getCounter(num);
        long newId = oldId + 1;
        context.putState("centroid_"+newId, serialize(newCentroid));
        context.incrCounter(num,1);
    }


    // 在聚类预测后添加更新逻辑
    public void updateCentroid(int clusterId, Map<String, Double> vector, Context context) {
        if(clusterId == -1){
            return;
        }
        // 积累 BATCH_TO_UPDATE 条数据后更新
        containerToBatchUpdate.computeIfAbsent(clusterId, k -> new ArrayList<>()).add(vector);

        if(containerToBatchUpdate.get(clusterId).size() >= BATCH_TO_UPDATE) {
            Map<String, Double> batchCentroid = calculateBatchCentroid(containerToBatchUpdate.get(clusterId));
            Map<String, Double> storedCentroid = deserialize(context.getState("centroid_"+clusterId));
            // 滑动平均更新
            storedCentroid.replaceAll((k, v) ->
                    0.9 * v + 0.1 * batchCentroid.getOrDefault(k, 0.0)
            );
            context.putState("centroid_"+clusterId, serialize(storedCentroid));
            containerToBatchUpdate.remove(clusterId);
        }
    }

    /**
     * 当某个簇的缓冲区（clusterBuffers）积累到100条数据时，
     * 该方法会遍历这些数据的所有特征维度，计算每个维度的均值作为临时质心。
     * @param batch
     * @return
     */
    Map<String, Double> calculateBatchCentroid(List<Map<String, Double>> batch) {
        Map<String, Double> centroid = new HashMap<>();
        int batchSize = batch.size();

        // 特征维度求和
        for (Map<String, Double> vec : batch) {
            vec.forEach((k, v) -> centroid.merge(k, v, Double::sum));
        }

        // 求均值
        centroid.replaceAll((k, v) -> v / batchSize);
        return centroid;
    }

    // 使用JSON序列化（需添加GSON依赖）
    private ByteBuffer serialize(Map<String, Double> vector) {
        return ByteBuffer.wrap(new Gson().toJson(vector).getBytes());
    }

    private Map<String, Double> deserialize(ByteBuffer buffer) {
        return new Gson().fromJson(
                new String(buffer.array(), buffer.position(), buffer.remaining()),
                new TypeToken<Map<String, Double>>(){}.getType()
        );
    }

    /**
     * 积累到一定数量后再生成新质心
     * @param buffer
     * @return
     */
    Map<String,Double> calculateCentroid(Queue<Map<String,Double>> buffer) {
        // 方案1：简单均值（适合低维数据）
        return buffer.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)));

        // 方案2：流形学习选择（参考网页6的特征抽取）
        // 使用Isomap/MDS降维后选择几何中心点[6](@ref)
    }

    // 基于数据分布的自适应阈值（参考网页7）
    public double calculateThreshold(List<Map<String, Double>> vectors) {
        List<Double> similarities = new ArrayList<>();
        // 采样计算相似度分布
        for (int i=0; i<100; i++) {
            int a = ThreadLocalRandom.current().nextInt(vectors.size());
            int b = ThreadLocalRandom.current().nextInt(vectors.size());
            similarities.add(cosineSimilarity(vectors.get(a), vectors.get(b)));
        }
        // 取均值+3倍标准差作为阈值
        double mean = similarities.stream().mapToDouble(d->d).average().orElse(0.8);
        double std = Math.sqrt(similarities.stream().mapToDouble(d->Math.pow(d-mean,2)).average().orElse(0.01));
        return mean + 3 * std;
    }
}
