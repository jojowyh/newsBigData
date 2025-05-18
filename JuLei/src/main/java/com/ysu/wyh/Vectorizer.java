package com.ysu.wyh;

import com.ysu.YuLiaoKu.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//向量化
public class Vectorizer {
    // 通用IDF（词条在整个语料库中的稀缺性）默认值（适用于无语料库场景）
    private static final double DEFAULT_IDF = 1.0;

    public static Map<String, Double> tfidf(List<String> words) {
        // 1. 计算TF(词频统计)
        Map<String, Double> tfMap = new HashMap<>();
        int totalWords = words.size();
        words.forEach(word ->
                tfMap.put(word, tfMap.getOrDefault(word, 0.0) + 1)
        );
        tfMap.replaceAll((k, v) -> v / totalWords);

        // 2. 应用通用IDF（），将这个两个值合并
        Map<String, Double> vector = new HashMap<>();
        tfMap.forEach((word, tf) ->{
            double idf = Util.calculateIDF(word);
            vector.put(word, tf * idf);
                });
        return vector;
    }

    //向量归一化？
    public static Map<String, Double> normalize(Map<String, Double> vector) {
        // 计算L2范数（欧几里得范数）
        double norm = 0.0;
        for (Double value : vector.values()) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);

        // 处理零范数（避免除以零）
        if (norm == 0) {
            return new HashMap<>(vector); // 返回原始向量副本
        }

        // 构造归一化后的向量
        Map<String, Double> normalized = new HashMap<>();
        for (Map.Entry<String, Double> entry : vector.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue() / norm);
        }
        return normalized;
    }
}
