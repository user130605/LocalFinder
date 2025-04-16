package com.example.user_service.vo;

import com.example.user_service.jpa.Role;
import lombok.Data;

@Data
public class ResponseCreate {

    private String email;

    private String nickname;

    private Role role;
}
