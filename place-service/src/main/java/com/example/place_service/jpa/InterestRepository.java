package com.example.place_service.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<InterestEntity, Integer> {
    Optional<InterestEntity> findByUserIdAndPlaceId(int userId, int placeId);
    List<InterestEntity> findByUserId(int userId);
}