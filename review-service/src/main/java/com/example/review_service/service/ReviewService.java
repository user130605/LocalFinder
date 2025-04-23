package com.example.review_service.service;

import com.example.review_service.dto.ReviewDto;
import com.example.review_service.vo.RequestReviewUpdate;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewDto reviewDto);
    List<ReviewDto> getReviewByPlaceId(int placeId);
    List<ReviewDto> getReviewByUserId(int userId);
    void updateReview(int reviewId, RequestReviewUpdate request);
    void deleteReview(int reviewId);
}
