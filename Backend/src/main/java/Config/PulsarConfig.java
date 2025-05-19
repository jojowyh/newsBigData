package Config;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PulsarConfig {

    @Value("${pulsar.broker-url}")
    private String BrokerUrl;

    @Value("${pulsar.TopicNames.InputTopics.RawNewsTopic}")
    private String RawNewsTopic;

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(BrokerUrl)
                .build();
    }

    @Bean
    public Producer<byte[]> newsProducer(PulsarClient client) throws PulsarClientException {
        return client.newProducer()
                .topic(RawNewsTopic)
                .enableBatching(true)
                .create();
    }


}
