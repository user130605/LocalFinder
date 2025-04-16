package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.jpa.UserEntity;
import com.example.user_service.jpa.UserRepository;
import com.example.user_service.kafka.KafkaProducer;
import com.example.user_service.service.UserService;
import com.example.user_service.vo.RequestCreate;
import com.example.user_service.vo.ResponseCreate;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-service")
public class UserController {
    private final KafkaProducer kafkaProducer;
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public UserController(KafkaProducer kafkaProducer, UserService userService, UserRepository userRepository) {
        this.kafkaProducer = kafkaProducer;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseCreate> createUser(@RequestBody RequestCreate requestCreate){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(requestCreate, UserDto.class);
        userDto = userService.createUser(userDto);

        ResponseCreate responseCreate = mapper.map(userDto, ResponseCreate.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreate);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseCreate> getUser(@PathVariable int userId){
        UserEntity userEntity = userRepository.findById(userId);

        ModelMapper mapper = new ModelMapper();
        ResponseCreate responseCreate = mapper.map(userEntity, ResponseCreate.class);

        return ResponseEntity.ok(responseCreate);
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
