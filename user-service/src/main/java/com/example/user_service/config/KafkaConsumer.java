package com.example.user_service.config;

import com.example.user_service.event.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "topicName", groupId = "groupId")
    public void consume(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            log.info("Received UserCreatedEvent: {}", event);

            // TODO: 여기서 이벤트 처리 로직 작성 (예: 사용자 정보 저장 등)

        } catch (Exception e) {
            log.error("Failed to parse UserCreatedEvent", e);
        }
    }
}
