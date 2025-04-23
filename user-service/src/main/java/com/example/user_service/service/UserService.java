package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.vo.RequestUserUpdate;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

public interface UserService {
    public UserDto createUser(UserDto userDto);
    public UserDto getUserById(int userId);
    public UserDto getUserByEmail(String email);

    UserDto login(String email, String password);
    void updateUser(int userId, RequestUserUpdate request);
    void deleteUser(int userId);
}
