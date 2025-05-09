package com.example.user_service.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    CUSTOMER, OWNER;

    // 소문자도 허용하도록
    @JsonCreator
    public static Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}

