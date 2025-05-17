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
        List<String> news = getNews(60);
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


            // 3. 聚类
            int i = clusterModel.predict(vec1, mockContext);
            System.out.println("Cluster:" + i);
            clusterModel.updateCentroid(i,vec1,mockContext);
        }




        // 4. 相似度验证
        //double similarity = SimilarityCalculator.cosineSimilarity(vec1, vec3);
        //System.out.printf("文本相似度: %.2f%%\n", similarity * 100);
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
