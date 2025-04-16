package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String password;
    private String nickname;
    private String role;
}
