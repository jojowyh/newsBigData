package com.ysu.Julei;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextProcessor {

    private static final Set<String> STOP_WORDS = Set.of("的", "了", "是", "在", "等", "并");

    // 文本清洗与分词
    public static List<String> preprocess(String text) {
        String cleaned = text.replaceAll("【.*?】", "")   // 去除标题头
                .replaceAll("\\d+", "")      // 去除数字
                .replaceAll("\\s+", " ");    // 合并空格

        // 中文分词（示例使用简单正则分词，实际需用分词库）
        return Arrays.stream(cleaned.split("[\\p{Punct}\\s]+"))
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }
}
