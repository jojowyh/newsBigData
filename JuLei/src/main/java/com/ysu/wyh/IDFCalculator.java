package com.ysu.wyh;

//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class IDFCalculator {
//    // 语料库文档总数（需在初始化时设置）
//    private static int totalDocs;
//    // 词条文档频率映射（需提前加载）
//    private static Map<String, Integer> docFreqMap;
//
//    // 初始化方法（需在程序启动时调用）
//    public static void initialize(int total, Map<String, Integer> freqMap) {
//        totalDocs = total;
//        docFreqMap = new ConcurrentHashMap<>(freqMap);
//    }
//
//    // 带平滑的IDF计算（参考sklearn的smooth_idf=True逻辑[4,7](@ref)）
//    public static double calculateIDF(String word) {
//        int df = docFreqMap.getOrDefault(word, 0);
//        return Math.log((totalDocs + 1.0) / (df + 1.0)) + 1;
//    }
//}
