package com.ysu.wyh;

//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class NewsCluster {
//    public static void cluster(List<Map<String, Double>> vectors) {
//        // 初始化质心（随机选一个）
//        Map<String, Double> centroid = new HashMap<>(vectors.get(0));
//
//        // 单次迭代（简化版，实际需循环至收敛）
//        List<Integer> assignments = vectors.stream()
//                .map(vec -> (SimilarityCalculator.cosineSimilarity(vec, centroid) > 0.5) ? 0 : 1)
//                .collect(Collectors.toList());
//
//        System.out.println("聚类结果: " + assignments);
//    }
//}
