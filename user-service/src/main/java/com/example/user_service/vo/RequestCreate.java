package com.example.user_service.vo;

import com.example.user_service.jpa.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestCreate {
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two charactors")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 4, message = "Password not be less than four charactors")
    private String password;

    @NotNull(message = "Nickname cannot be null")
    @Size(min = 4, message = "Nickname not be less than four charactors")
    private String nickname;

    @NotNull(message = "Role cannot be null")
    private Role role;
}
