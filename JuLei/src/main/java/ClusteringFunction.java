import com.ysu.YuLiaoKu.Util;
import com.ysu.wyh.ClusterModel;
import com.ysu.wyh.TextProcessor;
import com.ysu.wyh.Vectorizer;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class ClusteringFunction implements Function<String,String> {

    ClusterModel clusterModel;

    @Override
    public void initialize(Context context) throws Exception {
        //加载语料库
        String rootPath = "C:\\Users\\Asus\\Desktop\\大数据课程\\三级项目\\三级项目数据集\\data\\文本分类语料库";
        Util util = new Util(rootPath);
        context.incrCounter("numOfZhiXin",0);
        clusterModel = new ClusterModel(context);
        Function.super.initialize(context);
    }

    @Override
    public String process(String rawNews, Context context) throws Exception {


        // 步骤1：文本预处理
        List<String> words = TextProcessor.preprocess(rawNews);

        // 步骤2：向量化（基于TF-IDF）
        Map<String, Double> vector = Vectorizer.tfidf(words);


        // 步骤3：动态聚类
        int clusterId = clusterModel.predict(vector,context);

        // 步骤4：分发到对应Topic
        String outputTopic = "persistent://news/clusters/cluster-" + clusterId;
        context.newOutputMessage(outputTopic, Schema.STRING)
                .value(rawNews)
                .send();

         //更新聚类模型（可选）
        clusterModel.updateCentroid(clusterId,vector,context);
        return "";
    }
}
