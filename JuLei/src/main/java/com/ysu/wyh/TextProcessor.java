package com.ysu.wyh;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextProcessor {

    //private static final Set<String> STOP_WORDS = loadStopWords("stopwords.txt");
    private static final JiebaSegmenter SEGMENTER = initJieba();

    private static final Set<String> STOP_WORDS = Set.of("的", "了", "是", "在", "等", "并");

    private static JiebaSegmenter initJieba() {
        JiebaSegmenter seg = new JiebaSegmenter();
        return seg;
    }

    public static List<String> preprocess(String text) {
        // 深度清洗
        String cleaned = text.replaceAll("【.*?】|\\d+|[a-zA-Z]+|[\\pP\\pS]", "");

        // 精确模式分词
        List<String> words = SEGMENTER.sentenceProcess(cleaned);

        return words.stream()
                .filter(word -> word.length() > 1)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }
}
