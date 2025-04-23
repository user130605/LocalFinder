package com.example.place_service.service;

import com.example.place_service.config.KafkaProducer;
import com.example.place_service.dto.PlaceDto;
import com.example.place_service.event.PlaceCreatedEvent;
import com.example.place_service.jpa.*;
import com.example.place_service.vo.RequestPlaceUpdate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService{
    PlaceRepository placeRepository;
    KafkaProducer kafkaProducer;
    InterestRepository interestRepository;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository,
                            KafkaProducer kafkaProducer,
                            InterestRepository interestRepository) {
        this.placeRepository = placeRepository;
        this.kafkaProducer = kafkaProducer;
        this.interestRepository = interestRepository;
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

        List<PlaceEntity> placeEntities = placeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("해당 장소를 찾을 수 없습니다."));

        List<PlaceDto> placeDtos = new ArrayList<>();
        for (PlaceEntity entity : placeEntities) {
            PlaceDto dto = mapper.map(entity, PlaceDto.class);
            placeDtos.add(dto);
        }

        return placeDtos;
    }

    @Override
    @Transactional
    public void updatePlace(int ownerId, int placeId, RequestPlaceUpdate request) {
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        if (placeEntity.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("해당 장소에 대한 수정 권한이 없습니다.");
        }

        placeEntity.setName(request.getName());
        placeEntity.setBusinessHours(request.getBusinessHours());
        placeEntity.setAddress(request.getAddress());
        placeEntity.setCategory(request.getCategory());
        placeEntity.setDescription(request.getDescription());
        placeEntity.setPhone(request.getPhone());

        placeRepository.save(placeEntity);
    }

    @Override
    @Transactional
    public void deletePlace(int ownerId, int placeId) {
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        if (placeEntity.getOwnerId() != ownerId){
            throw new IllegalArgumentException("해당 장소에 대한 삭제 권한이 없습니다.");
        }

        placeRepository.delete(placeEntity);
    }

    @Override
    public void addInterest(int userId, int placeId) {
        // 중복 체크
        Optional<InterestEntity> existing = interestRepository.findByUserIdAndPlaceId(userId, placeId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 관심 장소에 등록된 항목입니다.");
        }

        InterestEntity interest = new InterestEntity();
        interest.setUserId(userId);
        interest.setPlaceId(placeId);

        interestRepository.save(interest);
    }

    @Override
    public List<PlaceDto> getInterestsByUserId(int userId) {
        List<InterestEntity> interestEntities = interestRepository.findByUserId(userId);

        List<PlaceDto> placeDtos = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        for (InterestEntity interest : interestEntities) {
            int placeId = interest.getPlaceId();
            PlaceEntity placeEntity = placeRepository.findById(placeId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

            PlaceDto placeDto = mapper.map(placeEntity, PlaceDto.class);
            placeDtos.add(placeDto);
        }

        return placeDtos;
    }

    @Override
    @Transactional
    public void deleteInterest(int interestId) {
        InterestEntity interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관심 장소를 찾을 수 없습니다."));

        interestRepository.delete(interest);
    }
}
