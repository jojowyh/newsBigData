package Service;

import entity.NewsItem;
import org.apache.pulsar.client.api.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableScheduling
@Service
public class NewsScheduler {

    @Autowired
    private GetNewsByApiService newsService;

    @Autowired
    private Producer<byte[]> newsProducer;

    // 每天9点、15点、21点执行（网页5[5](@ref), 网页6[6](@ref)）
    @Scheduled(cron = "0 0 9,15,21 * * ?")
    public void scheduleNewsCollection() throws IOException {
        List<NewsItem> news = newsService.fetchTechNews();

        news.forEach(item -> {
            try {
                // 序列化为JSON（推荐使用Jackson）
                String json = new ObjectMapper().writeValueAsString(item);
                newsProducer.sendAsync(json.getBytes(StandardCharsets.UTF_8));
            } catch (JsonProcessingException e) {
                log.error("消息序列化失败: {}", e.getMessage());
            }
        });
    }
}
