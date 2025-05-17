package com.ysu.Julei;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimilarityCalculator {
    // 计算两个向量的余弦相似度[6,7](@ref)
    public static double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        Set<String> commonWords = new HashSet<>(vec1.keySet());
        commonWords.retainAll(vec2.keySet());

        double dotProduct = commonWords.stream()
                .mapToDouble(word -> vec1.get(word) * vec2.get(word))
                .sum();

        double norm1 = Math.sqrt(vec1.values().stream().mapToDouble(v -> v*v).sum());
        double norm2 = Math.sqrt(vec2.values().stream().mapToDouble(v -> v*v).sum());

        return dotProduct / (norm1 * norm2 + 1e-6);  // 防止除以零
    }
}
