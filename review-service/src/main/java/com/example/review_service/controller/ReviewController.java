package com.example.review_service.controller;

import com.example.review_service.config.KafkaProducer;
import com.example.review_service.dto.ReviewDto;
import com.example.review_service.jpa.ReviewRepository;
import com.example.review_service.service.ReviewService;
import com.example.review_service.vo.RequestReview;
import com.example.review_service.vo.RequestReviewUpdate;
import com.example.review_service.vo.ResponseReview;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class ReviewController {
    private final KafkaProducer kafkaProducer;
    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    public ReviewController(KafkaProducer kafkaProducer, ReviewService reviewService, ReviewRepository reviewRepository) {
        this.kafkaProducer = kafkaProducer;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    // 리뷰 등록
    @PostMapping("/places/{placeId}/reviews")
    public ResponseEntity<ResponseReview> addReview(@RequestBody RequestReview requestReview){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ReviewDto reviewDto = mapper.map(requestReview, ReviewDto.class);
        reviewDto = reviewService.addReview(reviewDto);

        ResponseReview responseReview = mapper.map(reviewDto, ResponseReview.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseReview);
    }

    // 리뷰 조회 (placeId)
    @GetMapping("/places/{placeId}/reviews")
    public ResponseEntity<List<ResponseReview>> getReviewsByPlace(@PathVariable int placeId){
        List<ReviewDto> reviewDtos = reviewService.getReviewByPlaceId(placeId);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ResponseReview> result = new ArrayList<>();
        for (ReviewDto dto : reviewDtos) {
            result.add(mapper.map(dto, ResponseReview.class));
        }

        return ResponseEntity.ok(result);
    }

    // 리뷰 수정
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable int reviewId,
                                               @RequestBody @Valid RequestReviewUpdate request){
        reviewService.updateReview(reviewId, request);

        return ResponseEntity.ok("해당 리뷰가 수정되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable int reviewId){
        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("해당 리뷰가 삭제되었습니다.");
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "review-service 입니다.";
    }


}
