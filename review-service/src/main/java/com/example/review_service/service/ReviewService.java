package com.example.review_service.service;

import com.example.review_service.dto.ReviewDto;

public interface ReviewService {
    public ReviewDto addReview(ReviewDto reviewDto);
    public ReviewDto getReviewByPlaceId(int placeId);
    public ReviewDto getReviewByUserId(int userId);
}
