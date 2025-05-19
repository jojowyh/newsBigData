package entity;

import Util.KeywordsDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsItem {
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String url;
    private String poster;
    private String description;

    @JsonDeserialize(using = KeywordsDeserializer.class)
    private List<String> keywords;
}
