package axgiri.github.Gateway.Configuration;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import axgiri.github.Gateway.DTO.AuthRequestDTO;

@Service
public class KafkaMessageProducer {

    private final KafkaTemplate<String, AuthRequestDTO> kafkaTemplate;
    private static final String TOPIC = "task-requests";

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, AuthRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendMessage(AuthRequestDTO message) {
        String correlationId = java.util.UUID.randomUUID().toString();
        ProducerRecord<String, AuthRequestDTO> record = new ProducerRecord<>(TOPIC, message);
        record.headers().add("correlationId", correlationId.getBytes());
        kafkaTemplate.send(record);
        return correlationId;
    }
}

