package com.example.review_service.jpa;

import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    ReviewEntity findByPlaceId(int placeId);
    ReviewEntity findByUserId(int userId);
}
