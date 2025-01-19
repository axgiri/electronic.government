package axgiri.github.Gateway.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import axgiri.github.Gateway.Configuration.KafkaMessageProducer;
import axgiri.github.Gateway.Configuration.ResponseListener;
import axgiri.github.Gateway.DTO.AuthRequestDTO;

@Service
public class AuthService {

    private final KafkaMessageProducer producer;
    private final ResponseListener responseListener;

    @Autowired
    public AuthService(KafkaMessageProducer producer, ResponseListener responseListener) {
        this.producer = producer;
        this.responseListener = responseListener;
    }

    public boolean checkAuth(AuthRequestDTO request) throws Exception {
        String correlationId = producer.sendMessage(request);
        CompletableFuture<Boolean> future = responseListener.registerPending(correlationId);
        boolean result = future.get(3, TimeUnit.SECONDS);
        return result;
    }
}
