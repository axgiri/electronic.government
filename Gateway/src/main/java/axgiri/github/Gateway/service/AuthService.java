package axgiri.github.Gateway.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import axgiri.github.Gateway.configuration.KafkaMessageProducer;
import axgiri.github.Gateway.configuration.ResponseListener;
import axgiri.github.Gateway.dto.AuthRequestDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final KafkaMessageProducer producer;
    private final ResponseListener responseListener;

    public boolean checkAuth(AuthRequestDTO request) throws Exception {
        String correlationId = producer.sendMessage(request);
        CompletableFuture<Boolean> future = responseListener.registerPending(correlationId);
        boolean result = future.get(3, TimeUnit.SECONDS);
        return result;
    }
}
