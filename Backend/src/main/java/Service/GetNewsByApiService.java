package Service;

import Util.NewsParser;
import entity.NewsItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// 腾讯新闻API调用示例
@Service
public class GetNewsByApiService {

    @Autowired
    private NewsParser newsParser;

    @Value("${api.token}")
    private String tokenValue;

    private  final String NEWS_URL = "https://api.istero.com/resource/cctv/china/latest/news?token=" + tokenValue;


    /**
     * 调接口获取新闻
     * @return
     * @throws IOException
     */
    public List<NewsItem> fetchTechNews() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(NEWS_URL).get().build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        List<NewsItem> newsItems = newsParser.parseNews(resBody);
        return newsItems;
    }
}
