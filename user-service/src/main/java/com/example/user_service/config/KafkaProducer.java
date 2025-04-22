package com.example.user_service.config;

import com.example.user_service.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("user-created", event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka message send failed: {}", ex.getMessage(), ex);
            } else {
                log.info("Kafka message sent: {}", result.getProducerRecord().value());
            }
        });
    }

//    public void send(String topic, String message) {
//        kafkaTemplate.send(topic, message);
//    }
}

