package com.ysu.wyh;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public  class SimilarityCalculator {
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

    public double minDistance(Map<String, Double> vec, List<Map<String, Double>> centroids) {
        return centroids.stream()
                .mapToDouble(centroid -> cosineDistance(vec, centroid)) // 或欧式距离
                .min()
                .orElse(Double.MAX_VALUE); // 处理空质心列表
    }

    // 余弦相似度计算示例
    public double cosineDistance(Map<String, Double> v1, Map<String, Double> v2) {
        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String key : v1.keySet()) {
            dotProduct += v1.get(key) * v2.getOrDefault(key, 0.0);
            norm1 += Math.pow(v1.get(key), 2);
        }
        for (String key : v2.keySet()) {
            norm2 += Math.pow(v2.get(key), 2);
        }
        return 1 - (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));
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
