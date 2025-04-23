package com.example.place_service.service;

import com.example.place_service.dto.PlaceDto;
import com.example.place_service.vo.RequestPlaceUpdate;

import java.util.List;

public interface PlaceService{
    PlaceDto register(PlaceDto placeDto);
    List<PlaceDto> getPlacesByOwnerId(Integer ownerId);

    void updatePlace(int ownerId, int placeId, RequestPlaceUpdate request);
    void deletePlace(int ownerId, int placeId);

    void addInterest(int userId, int placeId);
    List<PlaceDto> getInterestsByUserId(int userId);
    void deleteInterest(int interestId);
}
