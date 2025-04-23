package com.example.place_service.controller;

import com.example.place_service.dto.PlaceDto;
import com.example.place_service.jpa.PlaceEntity;
import com.example.place_service.jpa.PlaceRepository;
import com.example.place_service.service.PlaceService;
import com.example.place_service.vo.RequestInterest;
import com.example.place_service.vo.RequestPlace;
import com.example.place_service.vo.RequestPlaceUpdate;
import com.example.place_service.vo.ResponsePlace;
import jakarta.validation.Valid;
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

    // 장소 등록
    @PostMapping("/places")
    public PlaceDto register(@RequestBody RequestPlace requestPlace){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PlaceDto placeDto = mapper.map(requestPlace, PlaceDto.class);
        placeDto = placeService.register(placeDto);

        return placeDto;
    }

    // placeId로 장소 조회
    @GetMapping("/places/{placeId}")
    public ResponseEntity<ResponsePlace> getPlace(@PathVariable int placeId){
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        ModelMapper mapper = new ModelMapper();
        ResponsePlace responsePlace = mapper.map(placeEntity, ResponsePlace.class);

        return ResponseEntity.ok(responsePlace);
    }

    // 내 장소 목록 조회
    @GetMapping("/places/my/{ownerId}")
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

    // 내 장소 상세 조회
    @GetMapping("/places/{ownerId}/{placeId}")
    public ResponseEntity<ResponsePlace> getMyPlace(@PathVariable int ownerId, @PathVariable int placeId){
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        if (placeEntity.getOwnerId() != ownerId)
            throw new RuntimeException("해당 장소에 접근 권한이 없습니다.");

        ModelMapper mapper = new ModelMapper();
        ResponsePlace responsePlace = mapper.map(placeEntity, ResponsePlace.class);

        return ResponseEntity.ok(responsePlace);
    }

    // 내 장소 수정
    @PatchMapping("/places/{ownerId}/{placeId}")
    public ResponseEntity<String> updatePlace(@PathVariable int ownerId,
                                              @PathVariable int placeId,
                                              @RequestBody @Valid RequestPlaceUpdate request){

        placeService.updatePlace(ownerId, placeId, request);

        return ResponseEntity.ok("장소 정보가 수정되었습니다.");
    }

    // 내 장소 삭제
    @DeleteMapping("/places/{ownerId}/{placeId}")
    public ResponseEntity<String> deletePlace(@PathVariable int ownerId,
                                              @PathVariable int placeId){

        placeService.deletePlace(ownerId, placeId);

        return ResponseEntity.ok("내 장소가 삭제되었습니다.");
    }

    // 관심 장소 추가
    @PostMapping("/interest")
    public ResponseEntity<String> addInterest(@RequestBody RequestInterest request){
        placeService.addInterest(request.getUserId(), request.getPlaceId());

        return ResponseEntity.ok("관심 장소가 추가되었습니다.");
    }

    // 관심 장소 목록 조회
    @GetMapping("/interest/{userId}")
    public ResponseEntity<List<ResponsePlace>> getInterest(@PathVariable int userId){
        List<PlaceDto> placeDtos = placeService.getInterestsByUserId(userId);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ResponsePlace> result = new ArrayList<>();
        for (PlaceDto dto : placeDtos) {
            result.add(mapper.map(dto, ResponsePlace.class));
        }

        return ResponseEntity.ok(result);
    }

    // 관심 장소 삭제
    @DeleteMapping("/interest/{interestId}")
    public ResponseEntity<String> deleteInterest(@PathVariable int interestId){
        placeService.deleteInterest(interestId);
        return ResponseEntity.ok("관심 장소가 삭제되었습니다.");
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "place-service 입니다.";
    }
}
