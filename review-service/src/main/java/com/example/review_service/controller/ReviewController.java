package com.example.review_service.controller;

import com.example.review_service.config.KafkaProducer;
import com.example.review_service.dto.ReviewDto;
import com.example.review_service.jpa.ReviewEntity;
import com.example.review_service.jpa.ReviewRepository;
import com.example.review_service.service.ReviewService;
import com.example.review_service.vo.RequestReview;
import com.example.review_service.vo.ResponseReview;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review-service")
public class ReviewController {
    private final KafkaProducer kafkaProducer;
    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    public ReviewController(KafkaProducer kafkaProducer, ReviewService reviewService, ReviewRepository reviewRepository) {
        this.kafkaProducer = kafkaProducer;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/places/{placeId}/reviews")
    public ResponseEntity<ResponseReview> addReview(@RequestBody RequestReview requestReview){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ReviewDto reviewDto = mapper.map(requestReview, ReviewDto.class);
        reviewDto = reviewService.addReview(reviewDto);

        ResponseReview responseReview = mapper.map(reviewDto, ResponseReview.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseReview);
    }

    @GetMapping("/places/{placeId}/reviews")
    public ResponseEntity<ResponseReview> getReviewsByPlace(@PathVariable int placeId){
        ReviewEntity reviewEntity = reviewRepository.findByPlaceId(placeId);

        ModelMapper mapper = new ModelMapper();
        ResponseReview responseReview = mapper.map(reviewEntity, ResponseReview.class);

        return ResponseEntity.ok(responseReview);
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "review-service 입니다.";
    }


}
