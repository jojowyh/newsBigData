package entity;

import lombok.Data;

import java.util.List;

@Data // Lombok注解自动生成getter/setter
public class NewsApiResponse {
    private Integer code;
    private List<NewsItem> data;
}
