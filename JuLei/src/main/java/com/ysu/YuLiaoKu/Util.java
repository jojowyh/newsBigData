package com.ysu.YuLiaoKu;

import com.ysu.wyh.TextProcessor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public class Util {
    public List<Set<String>> processedDocs = new ArrayList<>();
    public static Map<String, Integer> docFreqMap = new HashMap<>();
    static int totalDocs = 0;
    //语料库目录的路径
    String rootPath;
    public Util(String rootPath) {
        this.rootPath = rootPath;
        traverseWithJava8(rootPath);
    }

    @SneakyThrows
    public void traverseWithJava8(String rootPath) {
        try {
            //对于每个text文件，分好词存到一个set里，然后再把这个set存到processedDocs里
            Files.walk(Paths.get(rootPath))
                    //.filter(Files::isRegularFile) // 过滤出文件
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt")) // 过滤文本文件
                    .forEach(path -> {
                        try {
                            processTextFile(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }); // 处理文件

            // 统计每个词出现的文档数
            for (Set<String> docWords : processedDocs) {
                for (String word : docWords) {
                    docFreqMap.put(word, docFreqMap.getOrDefault(word, 0) + 1);
                }
            }

             totalDocs = processedDocs.size(); // 总文档数
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public  void processTextFile(Path path) throws IOException {

        try (InputStream inputStream = new FileInputStream(String.valueOf(path))) {
            String content = IOUtils.toString(inputStream,"GBK");
            List<String> words = TextProcessor.preprocess(content); // 分词+清洗（参考网页7[7](@ref)）
            processedDocs.add(new HashSet<>(words)); // 用Set去重，统计文档频率
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 带平滑的IDF计算（参考sklearn的smooth_idf=True逻辑[4,7](@ref)）
    public static double calculateIDF(String word) {
        int df = docFreqMap.getOrDefault(word, 0);
        return Math.log((totalDocs + 1.0) / (df + 1.0)) + 1;
    }


}
