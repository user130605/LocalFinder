package com.example.user_service.service;

import com.example.user_service.config.KafkaProducer;
import com.example.user_service.dto.UserDto;
import com.example.user_service.event.UserCreatedEvent;
import com.example.user_service.jpa.UserEntity;
import com.example.user_service.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    KafkaProducer kafkaProducer;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setPassword("encryptedPassword");

        userRepository.save(userEntity);

        // Kafka 이벤트 발행
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(userEntity.getId())
                .email(userEntity.getEmail())
                .nickname(userEntity.getNickname())
                .build();

        kafkaProducer.sendUserCreatedEvent(event);

        return userDto;
    }

    @Override
    public UserDto getUserById(int userId) {
        UserEntity userEntity = userRepository.findById(userId);

        if (userEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);

        return userDto;
    }
}
