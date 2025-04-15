package com.example.user_service.controller;

import com.example.user_service.kafka.KafkaProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
public class UserController {
    private final KafkaProducer kafkaProducer;
    public UserController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        kafkaProducer.send("user-created", message);
        return "Sent to Kafka: " + message;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "user-service 입니다.";
    }
}
