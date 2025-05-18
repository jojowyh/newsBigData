package com.ysu.wyh;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.*;
import java.util.stream.Collectors;

public class TextProcessor {

    //private static final Set<String> STOP_WORDS = loadStopWords("stopwords.txt");
    private static final JiebaSegmenter SEGMENTER = initJieba();

    private static final Set<String> STOP_WORDS = Set.of(
            // 代词类[2,5](@ref)
            "我", "你", "他", "她", "它", "我们", "你们", "他们", "这", "那", "其", "之",

            // 介词连词[2,5](@ref)
            "对", "在", "给", "与", "由于", "关于", "为了", "按照", "通过", "和", "但是",
            "因为", "所以", "虽然", "即使",

            // 助词语气[2,4](@ref)
            "的", "地", "得", "了", "过", "吗", "呢", "吧", "啊", "呀",

            // 高频虚词[2,5](@ref)
            "是", "有", "会", "可以", "可能", "应该", "需要", "能够", "例如", "然后"
    );

    private static JiebaSegmenter initJieba() {
        JiebaSegmenter seg = new JiebaSegmenter();
        return seg;
    }

    public static List<String> preprocess(String text) {
        // 深度清洗
        // 保留中文标点中的句号、问号、感叹号（网页5）
        String cleaned = text.replaceAll("【.*?】|\\d+|[a-zA-Z]+|[^\u4e00-\u9fa5。？！]", "");

        // 精确模式分词
        List<String> words = SEGMENTER.sentenceProcess(cleaned);

        return words.stream()
                .filter(word -> word.length() > 1)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }
}
