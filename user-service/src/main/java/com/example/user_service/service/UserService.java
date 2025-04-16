package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import org.springframework.stereotype.Service;

public interface UserService {
    public UserDto createUser(UserDto userDto);
    public UserDto getUserById(int userId);
    public UserDto getUserByEmail(String email);
}
