package com.ysu.wyh;

import org.apache.commons.io.IOUtils;
import org.apache.pulsar.functions.api.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.ysu.wyh.SimilarityCalculator.cosineSimilarity;

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
//        for(String s : news) {
//            // 1. 预处理
//            List<String> words1 = TextProcessor.preprocess(s);
//
//
//            // 2. 向量化
//            Map<String, Double> vec1 = Vectorizer.tfidf(words1);
//            vec1 = Vectorizer.normalize(vec1);
//
//            // 3. 聚类
//            int i = clusterModel.predict(vec1, mockContext);
//            System.out.println("Cluster:" + i);
//            clusterModel.updateCentroid(i,vec1,mockContext);
//        }

        String s = "1、我哈打卡机阿萨德建卡户大花洒漏打卡哈DSL ";
        List<String> preprocess = TextProcessor.preprocess(s);
        Map<String, Double> tfidf = Vectorizer.tfidf(preprocess);
        tfidf = Vectorizer.normalize(tfidf);
        int i = clusterModel.predict(tfidf, mockContext);
        clusterModel.updateCentroid(i,tfidf,mockContext);

        String s1 = "苹果手机价格";
        String s2 = "华为手机价格";
        List<String> preprocess1 = TextProcessor.preprocess(s1);
        List<String> preprocess2 = TextProcessor.preprocess(s2);
        Map<String, Double> tfidf1 = Vectorizer.tfidf(preprocess1);
        Map<String, Double> tfidf2 = Vectorizer.tfidf(preprocess2);
        double similarity = cosineSimilarity(tfidf1, tfidf2);

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
