package com.example.review_service.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestReview {
    @NotNull(message = "Place ID cannot be null")
    private int placeId;

    @NotNull(message = "User ID cannot be null")
    private int userId;

    @NotNull(message = "Content cannot be null")
    private String content;

    @NotNull(message = "Rating cannot be null")
    private int rating;

}
