package Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.NewsApiResponse;
import entity.NewsItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsParser {

    //拉取到的响应格式
//    {
//        "code": 200,
//            "data": [
//        {
//            "title": "4月份经济数据释放哪些信号...",
//                "time": "2025-05-19 13:46:20",
//                "url": "https://...",
//                "poster": "https://...",
//                "description": "透过数据看经济...",
//                "keywords": "4月份 经济数据 释放 信号..."
//        }
//  ]
//    }
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<NewsItem> parseNews(String json) throws JsonProcessingException {
        NewsApiResponse response = mapper.readValue(json, NewsApiResponse.class);
        if (response.getCode() != 200) {
            throw new IllegalStateException("API返回非200状态码");
        }
        return response.getData();
    }


}
