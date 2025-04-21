package com.example.review_service.vo;

import lombok.Data;

@Data
public class ResponseReview {

    private int placeId;

    private int userId;

    private String content;

    private int rating;

}
