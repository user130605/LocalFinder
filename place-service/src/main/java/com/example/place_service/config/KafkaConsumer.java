package com.example.place_service.config;

import com.example.place_service.event.ReviewAddedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "topicName", groupId = "groupId")
    public void consume(String message) {
        log.info("Consumed message: {}", message);
    }

    @KafkaListener(topics = "review-added", groupId = "place-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleReviewAdded(@Payload ReviewAddedEvent event) {
        log.info("Kafka message received: id={}, content={}", event.getReviewId(), event.getContent());
    }
}
