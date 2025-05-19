package com.ysu.wyh;

import org.apache.commons.io.IOUtils;
import org.apache.pulsar.functions.api.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Test {

    @org.junit.jupiter.api.Test
    public void test() throws Exception {
//
        List<String> news = getNews(1000);
        MockFunction mockFunction = new MockFunction();
        MockContext mockContext = new MockContext();
        mockContext.incrCounter("numOfZhiXin",0);
        ClusterModel clusterModel = new ClusterModel(mockContext);
        mockFunction.initialize(mockContext);
        for(String s : news) {
            // 1. 预处理
            List<String> words1 = TextProcessor.preprocess(s);


            // 2. 向量化
            Map<String, Double> vec1 = Vectorizer.tfidf(words1);
            vec1 = Vectorizer.normalize(vec1);

            // 3. 聚类
            int i = clusterModel.predict(vec1, mockContext);
            System.out.println("Cluster:" + i);
            clusterModel.updateCentroid(i,vec1,mockContext);
        }

        String s = "嗯，用户的问题是关于如何正确填写SparkSession的创建代码，以及在Linux上是否需要启动Spark进程。我需要结合用户提供的搜索内容来解答。\n" +
                "\n" +
                "首先，用户给出的代码片段是使用SparkSession.builder()来创建会话，这里涉及到应用名称和master的设置。根据搜索结果，比如网页1和网页2提到，SparkSession是Spark 2.0后的统一入口，替代了之前的SparkContext和SQLContext。创建时可以通过builder模式配置，比如设置appName和master。网页1中示例代码使用了.local[]作为master，表示本地运行，使用所有可用核心。所以用户代码中的master(\"local[]\")是正确的，不需要在Linux上启动独立的Spark集群进程，尤其是在本地模式下。\n" +
                "\n" +
                "接下来，用户问是否需要启动Spark进程。根据网页6、网页7、网页10和网页11，本地模式（local）不需要启动额外的守护进程，比如master或worker。但在集群模式（如Standalone、YARN）下则需要启动相关进程。网页6明确指出，local模式解压即可使用，无需启动master或worker。而网页10和11提到，如果使用集群模式，比如Standalone，需要启动master和worker节点，但用户当前代码是local模式，所以不需要。";
        List<String> preprocess = TextProcessor.preprocess(s);
        Map<String, Double> tfidf = Vectorizer.tfidf(preprocess);
        tfidf = Vectorizer.normalize(tfidf);
        int i = clusterModel.predict(tfidf, mockContext);
        clusterModel.updateCentroid(i,tfidf,mockContext);
//
//        String s1 = "苹果手机价格";
//        String s2 = "华为手机价格";
//        List<String> preprocess1 = TextProcessor.preprocess(s1);
//        List<String> preprocess2 = TextProcessor.preprocess(s2);
//        Map<String, Double> tfidf1 = Vectorizer.tfidf(preprocess1);
//        Map<String, Double> tfidf2 = Vectorizer.tfidf(preprocess2);
//        double similarity = cosineSimilarity(tfidf1, tfidf2);

// 预处理后向量：
// s1: {"苹果":0.707, "手机":0.707, "价格":0.707}
// s2: {"华为":0.707, "手机":0.707, "价格":0.707}
// 实际输出：0.666（合理区间）


    }


    /**
     *
     * @return
     */
    public List<String> getNews(int num){
        List<String> news = new ArrayList<String>();
        String rootPath = "C:\\Users\\Asus\\Desktop\\大数据课程\\三级项目\\三级项目数据集\\data\\文本分类语料库";
        try {
            //对于每个text文件，分好词存到一个set里，然后再把这个set存到processedDocs里
            Files.walk(Paths.get(rootPath))
                    //.filter(Files::isRegularFile) // 过滤出文件
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))// 过滤文本文件
                    .limit(num)
                    .forEach(path -> {
                        try (InputStream inputStream = new FileInputStream(String.valueOf(path))) {
                            String content = IOUtils.toString(inputStream, "GBK");
                            news.add(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }); // 处理文件

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return news;
    }

}
