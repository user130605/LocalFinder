package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.jpa.UserEntity;
import com.example.user_service.jpa.UserRepository;
import com.example.user_service.config.KafkaProducer;
import com.example.user_service.service.UserService;
import com.example.user_service.vo.RequestLogin;
import com.example.user_service.vo.RequestUser;
import com.example.user_service.vo.RequestUserUpdate;
import com.example.user_service.vo.ResponseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User")
@RestController
@RequestMapping("/")
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

    @Operation(summary = "회원 등록", description = "회원 등록입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class))
            })
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userDto = userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @Operation(summary = "로그인", description = "로그인입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class))
            })
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseUser> login(@RequestBody RequestLogin requestLogin) {
        UserDto userDto = userService.login(requestLogin.getEmail(), requestLogin.getPassword());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.ok(responseUser);
    }

    @Operation(summary = "내 정보 조회", description = "내 정보 조회입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class))
            })
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable int userId){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        ModelMapper mapper = new ModelMapper();
        ResponseUser responseUser = mapper.map(userEntity, ResponseUser.class);

        return ResponseEntity.ok(responseUser);
    }

    @Operation(summary = "내 정보 수정", description = "내 정보 수정입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable int userId,
                                             @RequestBody @Valid RequestUserUpdate requestUserUpdate) {

        userService.updateUser(userId, requestUserUpdate);
        return ResponseEntity.ok("내 정보가 수정되었습니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
