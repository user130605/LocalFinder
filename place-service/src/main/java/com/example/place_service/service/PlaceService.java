package com.example.place_service.service;

import com.example.place_service.dto.PlaceDto;

import java.util.List;

public interface PlaceService{
    public PlaceDto register(PlaceDto placeDto);
    List<PlaceDto> getPlacesByOwnerId(Integer ownerId);
}
