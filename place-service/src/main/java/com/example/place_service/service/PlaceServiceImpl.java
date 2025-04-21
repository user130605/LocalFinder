package com.example.place_service.service;

import com.example.place_service.config.KafkaProducer;
import com.example.place_service.dto.PlaceDto;
import com.example.place_service.event.PlaceCreatedEvent;
import com.example.place_service.jpa.PlaceEntity;
import com.example.place_service.jpa.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService{
    PlaceRepository placeRepository;
    KafkaProducer kafkaProducer;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository, KafkaProducer kafkaProducer) {
        this.placeRepository = placeRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public PlaceDto register(PlaceDto placeDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PlaceEntity placeEntity = mapper.map(placeDto, PlaceEntity.class);

        placeRepository.save(placeEntity);

        // Kafka 이벤트 발행
        PlaceCreatedEvent event = PlaceCreatedEvent.builder()
                .placeId(placeEntity.getId())
                .name(placeEntity.getName())
                .ownerId(placeEntity.getOwnerId())
                .build();

        kafkaProducer.sendPlaceCreatedEvent(event);

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
