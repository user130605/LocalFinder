package com.example.review_service.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private int placeId;
    private int userId;
    private String content;
    private int rating;
}
