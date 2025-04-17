package com.example.place_service.service;

import com.example.place_service.dto.PlaceDto;
import com.example.place_service.jpa.PlaceEntity;
import com.example.place_service.jpa.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService{
    PlaceRepository placeRepository;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public PlaceDto register(PlaceDto placeDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PlaceEntity placeEntity = mapper.map(placeDto, PlaceEntity.class);

        placeRepository.save(placeEntity);

        return placeDto;
    }

    @Override
    public List<PlaceDto> getPlacesByOwnerId(Integer ownerId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<PlaceEntity> placeEntities = placeRepository.findByOwnerId(ownerId);

        List<PlaceDto> placeDtos = new ArrayList<>();
        for (PlaceEntity entity : placeEntities) {
            PlaceDto dto = mapper.map(entity, PlaceDto.class);
            placeDtos.add(dto);
        }

        return placeDtos;
    }
}
