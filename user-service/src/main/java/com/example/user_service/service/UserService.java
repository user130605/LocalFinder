package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.vo.RequestUserUpdate;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto login(String email, String password);
    void updateUser(int userId, RequestUserUpdate request);
    void deleteUser(int userId);

    UserDto getUserDetailsByEmail(String userName);
}
