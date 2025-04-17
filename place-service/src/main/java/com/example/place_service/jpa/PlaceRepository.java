package com.example.place_service.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlaceRepository extends CrudRepository<PlaceEntity, Integer>{
    PlaceEntity findById(int placeId);
    List<PlaceEntity> findByOwnerId(int ownerId);
}
