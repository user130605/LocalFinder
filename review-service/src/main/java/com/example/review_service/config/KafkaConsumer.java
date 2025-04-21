package com.example.review_service.config;

import com.example.review_service.event.PlaceCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "user-created", groupId = "review-group")
    public void consume(String message) {
        log.info("Consumed message: {}", message);
    }

    @KafkaListener(topics = "place-created", groupId = "review-group", containerFactory = "kafkaListenerContainerFactory")
    public void handlePlaceCreated(@Payload PlaceCreatedEvent event) {
        log.info("Kafka message received: id={}, name={}", event.getPlaceId(), event.getName());
    }
}
