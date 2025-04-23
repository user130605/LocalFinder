package com.example.user_service.vo;

import lombok.Data;

@Data
public class RequestLogin {
    private String email;
    private String password;
}