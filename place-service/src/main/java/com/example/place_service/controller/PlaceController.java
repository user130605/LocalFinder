package com.example.place_service.controller;

import com.example.place_service.dto.PlaceDto;
import com.example.place_service.jpa.PlaceEntity;
import com.example.place_service.jpa.PlaceRepository;
import com.example.place_service.service.PlaceService;
import com.example.place_service.vo.RequestPlace;
import com.example.place_service.vo.ResponsePlace;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/place-service")
public class PlaceController {
    PlaceService placeService;
    PlaceRepository placeRepository;

    @Autowired
    public PlaceController(PlaceService placeService, PlaceRepository placeRepository) {
        this.placeService = placeService;
        this.placeRepository = placeRepository;
    }

    @PostMapping("/places")
    public PlaceDto register(@RequestBody RequestPlace requestPlace){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PlaceDto placeDto = mapper.map(requestPlace, PlaceDto.class);
        placeDto = placeService.register(placeDto);

        return placeDto;
    }

    @GetMapping("/id/{placeId}")
    public ResponseEntity<ResponsePlace> getPlace(@PathVariable int placeId){
        PlaceEntity placeEntity = placeRepository.findById(placeId);

        ModelMapper mapper = new ModelMapper();
        ResponsePlace responsePlace = mapper.map(placeEntity, ResponsePlace.class);

        return ResponseEntity.ok(responsePlace);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ResponsePlace>> getMyPlaces(@PathVariable int ownerId){
        List<PlaceDto> placeDtos = placeService.getPlacesByOwnerId(ownerId);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ResponsePlace> result = new ArrayList<>();
        for (PlaceDto dto : placeDtos) {
            result.add(mapper.map(dto, ResponsePlace.class));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "place-service 입니다.";
    }
}
