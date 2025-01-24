package github.axgiri.AuthenticationService.Configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.axgiri.AuthenticationService.requests.AuthRequest;
import github.axgiri.AuthenticationService.Security.TaskSecurityService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final KafkaConsumer<String, String> consumer; 
    private final KafkaProducer<String, String> producer;
    private final TaskSecurityService taskSecurityService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String requestTopic = "task-requests";
    private final String responseTopic = "task-responses";

    public KafkaConfig(String bootstrapServers, TaskSecurityService taskSecurityService) {
        this.taskSecurityService = taskSecurityService;

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "auth-service-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(requestTopic));

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);
    }

    public void run() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> record : records) {
                    String correlationId = extractCorrelationId(record.headers());
                    if (correlationId == null) {
                        System.out.println("no correlationId in headers - can't correlate response");
                        continue;
                    }

                    String jsonRequest = record.value();
                    AuthRequest requestDTO = parseJson(jsonRequest);
                    boolean isAuthorized = checkAuth(requestDTO);
                    String responsePayload = String.valueOf(isAuthorized);
                    ProducerRecord<String, String> responseRecord = new ProducerRecord<>(responseTopic, responsePayload);
                    responseRecord.headers()
                        .add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
                    producer.send(responseRecord);
                    System.out.printf("processed request with correlationId=%s; result=%s\n",
                            correlationId, responsePayload);
                }
            }
        } finally {
            consumer.close();
            producer.close();
        }
    }

    private String extractCorrelationId(Headers headers) {
        Header header = headers.lastHeader("correlationId");
        if (header == null) return null;
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    private AuthRequest parseJson(String json) {
        try {
            return objectMapper.readValue(json, AuthRequest.class);
        } catch (IOException e) {
            System.err.println("failed to parse JSON to AuthRequestDTO: " + e.getMessage());
            throw new RuntimeException("invalid JSON format for AuthRequestDTO", e);
        }
    }
    
    public boolean checkAuth(AuthRequest requestDTO) {
        return taskSecurityService.validateRequest(requestDTO);
    }
}

