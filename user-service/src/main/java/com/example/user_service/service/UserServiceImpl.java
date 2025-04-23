package com.example.user_service.service;

import com.example.user_service.config.KafkaProducer;
import com.example.user_service.dto.UserDto;
import com.example.user_service.event.UserCreatedEvent;
import com.example.user_service.jpa.UserEntity;
import com.example.user_service.jpa.UserRepository;
import com.example.user_service.vo.RequestUserUpdate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    KafkaProducer kafkaProducer;
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           KafkaProducer kafkaProducer,
                           BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

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
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (userEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (userEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDto login(String email, String password) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        ModelMapper mapper = new ModelMapper();
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    @Transactional
    public void updateUser(int userId, RequestUserUpdate request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        userEntity.setNickname(request.getNickname());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            userEntity.setPassword(encodedPassword);
        }

        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        userRepository.delete(userEntity);
    }
}
