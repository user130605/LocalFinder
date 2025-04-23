package com.example.review_service.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    Optional<List<ReviewEntity>> findByPlaceId(int placeId);
    Optional<List<ReviewEntity>> findByUserId(int userId);
}
