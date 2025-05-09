package com.example.user_service.service;

import com.example.user_service.config.KafkaProducer;
import com.example.user_service.dto.UserDto;
import com.example.user_service.event.UserCreatedEvent;
import com.example.user_service.jpa.UserEntity;
import com.example.user_service.jpa.UserRepository;
import com.example.user_service.vo.RequestUserUpdate;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           KafkaProducer kafkaProducer,
                           BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자가 로그인을 시도할 때 호출돼.
    // 예를 들어, /login으로 이메일(email)과 비밀번호(password)를 보내면,
    // Spring Security가 이 메서드를 자동으로 호출해서 이메일로 사용자 정보를 조회한다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // Spring Security가 내부적으로 사용하는 User 객체를 만들어서 리턴
        // 이 객체에 이메일, 비밀번호, 계정 활성화 여부, 권한 정보 등을 넘겨준다.
        // 권한은 new ArrayList<>()로 비워져 있어서, 지금은 따로 역할(role) 체크를 하지 않는다.
        return new User(userEntity.getEmail(), userEntity.getPassword(),
                true, true, true, true,
                new ArrayList<>());
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

        UserDto returnDto = mapper.map(userEntity, UserDto.class);
        return returnDto;
    }

//    @Override
//    public UserDto getUserById(int userId) {
//        UserEntity userEntity = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
//
//        if (userEntity == null)
//            return null;
//
//        ModelMapper mapper = new ModelMapper();
//        UserDto userDto = mapper.map(userEntity, UserDto.class);
//
//        return userDto;
//    }

//    @Override
//    public UserDto getUserByEmail(String email) {
//        UserEntity userEntity = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
//
//        if (userEntity == null)
//            return null;
//
//        ModelMapper mapper = new ModelMapper();
//        UserDto userDto = mapper.map(userEntity, UserDto.class);
//
//        return userDto;
//    }

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

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}
