package com.example.review_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "user-created", groupId = "review-group")
    public void consume(String message) {
        log.info("Consumed message: {}", message);
    }
}
