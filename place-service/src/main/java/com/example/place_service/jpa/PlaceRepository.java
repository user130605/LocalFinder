package com.example.place_service.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends CrudRepository<PlaceEntity, Integer>{
    Optional<PlaceEntity> findById(int placeId);
    Optional<List<PlaceEntity>> findByOwnerId(int ownerId);
}
