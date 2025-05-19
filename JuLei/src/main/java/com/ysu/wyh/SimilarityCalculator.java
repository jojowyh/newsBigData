package com.ysu.wyh;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.ysu.wyh.Vectorizer.normalize;

public  class SimilarityCalculator {
    // 计算两个向量的***余弦相似度***
    public static double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        Map<String, Double> normVec1 = normalize(vec1); // 网页6强调必须
        Map<String, Double> normVec2 = normalize(vec2);

        // 复用稀疏向量优化计算（网页4方法）
        double dotProduct = 0.0;
        for (String key : normVec1.keySet()) {
            if (normVec2.containsKey(key)) {
                dotProduct += normVec1.get(key) * normVec2.get(key);
            }
        }
        return dotProduct; // 因已归一化，norm1*norm2=1
    }

    public double minDistance(Map<String, Double> vec, List<Map<String, Double>> centroids) {
        return centroids.stream()
                .mapToDouble(centroid -> cosineDistance(vec, centroid)) // 或欧式距离
                .min()
                .orElse(Double.MAX_VALUE); // 处理空质心列表
    }

    // ***余弦距离***计算示例
    public  static double  cosineDistance(Map<String, Double> v1, Map<String, Double> v2) {
        return 1 - cosineSimilarity(v1, v2); // 严格数学关系
    }

    public int weightedRandomSelect(List<Double> distances) {
        // 计算距离平方的累积概率
        List<Double> squaredDistances = distances.stream()
                .map(d -> d * d)
                .collect(Collectors.toList());
        double total = squaredDistances.stream().mapToDouble(Double::doubleValue).sum();

        // 生成随机数并选择
        double random = ThreadLocalRandom.current().nextDouble(total);
        double cumulative = 0.0;
        for (int i=0; i<squaredDistances.size(); i++) {
            cumulative += squaredDistances.get(i);
            if (cumulative >= random) {
                return i;
            }
        }
        return distances.size() - 1; // 容错
    }
}
