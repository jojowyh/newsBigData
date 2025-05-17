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
}
