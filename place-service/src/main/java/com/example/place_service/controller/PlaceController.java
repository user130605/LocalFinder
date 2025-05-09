package com.example.place_service.controller;

import com.example.place_service.dto.PlaceDto;
import com.example.place_service.jpa.PlaceEntity;
import com.example.place_service.jpa.PlaceRepository;
import com.example.place_service.service.PlaceService;
import com.example.place_service.vo.RequestInterest;
import com.example.place_service.vo.RequestPlace;
import com.example.place_service.vo.RequestPlaceUpdate;
import com.example.place_service.vo.ResponsePlace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Place")
@RestController
@RequestMapping("/")
public class PlaceController {
    PlaceService placeService;
    PlaceRepository placeRepository;

    @Autowired
    public PlaceController(PlaceService placeService, PlaceRepository placeRepository) {
        this.placeService = placeService;
        this.placeRepository = placeRepository;
    }

    @Operation(summary = "장소 등록", description = "장소 등록입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlaceDto.class))
            })
    })
    @PostMapping("/places")
    public ResponseEntity<ResponsePlace> register(@RequestBody RequestPlace requestPlace){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PlaceDto placeDto = mapper.map(requestPlace, PlaceDto.class);
        placeDto = placeService.register(placeDto);

//        return placeDto;

        ResponsePlace responsePlace = mapper.map(placeDto, ResponsePlace.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responsePlace);
    }

    @Operation(summary = "특정 장소 조회", description = "placeId로 특정 장소를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePlace.class))
            })
    })
    @GetMapping("/places/{placeId}")
    public ResponseEntity<ResponsePlace> getPlace(@PathVariable int placeId){
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        ModelMapper mapper = new ModelMapper();
        ResponsePlace responsePlace = mapper.map(placeEntity, ResponsePlace.class);

        return ResponseEntity.ok(responsePlace);
    }

    @Operation(summary = "내 장소 목록 조회", description = "ownerId로 내가 등록한 장소 목록를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ResponsePlace.class)))
            })
    })
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

    @Operation(summary = "내 장소 조회", description = "내가 등록한 특정 장소를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePlace.class))
            })
    })
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

    @Operation(summary = "내 장소 수정", description = "내가 등록한 특정 장소를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @PatchMapping("/places/{ownerId}/{placeId}")
    public ResponseEntity<String> updatePlace(@PathVariable int ownerId,
                                              @PathVariable int placeId,
                                              @RequestBody @Valid RequestPlaceUpdate request){

        placeService.updatePlace(ownerId, placeId, request);

        return ResponseEntity.ok("장소 정보가 수정되었습니다.");
    }

    @Operation(summary = "내 장소 삭제", description = "내가 등록한 특정 장소를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @DeleteMapping("/places/{ownerId}/{placeId}")
    public ResponseEntity<String> deletePlace(@PathVariable int ownerId,
                                              @PathVariable int placeId){

        placeService.deletePlace(ownerId, placeId);

        return ResponseEntity.ok("내 장소가 삭제되었습니다.");
    }

    @Operation(summary = "관심 장소 추가", description = "관심 장소 추가입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @PostMapping("/interest")
    public ResponseEntity<String> addInterest(@RequestBody RequestInterest request){
        placeService.addInterest(request.getUserId(), request.getPlaceId());

        return ResponseEntity.ok("관심 장소가 추가되었습니다.");
    }

    @Operation(summary = "내 관심 장소 목록 조회", description = "내가 추가한 관심 장소 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ResponsePlace.class)))
            })
    })
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

    @Operation(summary = "관심 장소 삭제", description = "관심 장소 목록에서 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            })
    })
    @DeleteMapping("/interest/{interestId}")
    public ResponseEntity<String> deleteInterest(@PathVariable int interestId){
        placeService.deleteInterest(interestId);
        return ResponseEntity.ok("관심 장소가 삭제되었습니다.");
    }
}
