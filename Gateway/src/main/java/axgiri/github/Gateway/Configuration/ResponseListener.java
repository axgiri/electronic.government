package axgiri.github.Gateway.Configuration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

@Component
public class ResponseListener {

    private final KafkaConsumer<String, String> consumer;
    private final Thread listenerThread;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    private volatile boolean running = true;
    private static final String RESPONSE_TOPIC = "task-responses";

    @Autowired
    public ResponseListener(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gateway-response-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(RESPONSE_TOPIC));
        listenerThread = new Thread(this::pollLoop, "ResponseListenerThread");
        listenerThread.start();
    }

    private void pollLoop() {
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> record : records) {
                    String correlationId = extractCorrelationId(record.headers());
                    if (correlationId == null) {
                        System.out.println("no correlationId header, skipping message");
                        continue;
                    }
                    String payload = record.value();
                    boolean result = Boolean.parseBoolean(payload);
                    CompletableFuture<Boolean> future = pendingRequests.remove(correlationId);
                    if (future != null) {
                        future.complete(result);
                    } else {
                        System.out.println("no pending request found for correlationId=" + correlationId);
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }

    public CompletableFuture<Boolean> registerPending(String correlationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }

    public void shutdown() {
        running = false;
        listenerThread.interrupt();
        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String extractCorrelationId(Headers headers) {
        Header header = headers.lastHeader("correlationId");
        if (header == null) return null;
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
