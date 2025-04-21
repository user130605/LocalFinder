package com.example.review_service.service;

import com.example.review_service.config.KafkaProducer;
import com.example.review_service.dto.ReviewDto;
import com.example.review_service.event.ReviewAddedEvent;
import com.example.review_service.jpa.ReviewEntity;
import com.example.review_service.jpa.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    KafkaProducer kafkaProducer;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, KafkaProducer kafkaProducer) {
        this.reviewRepository = reviewRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public ReviewDto addReview(ReviewDto reviewDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ReviewEntity reviewEntity = mapper.map(reviewDto, ReviewEntity.class);

        reviewRepository.save(reviewEntity);

        // Kafka 이벤트 발행
        ReviewAddedEvent event = ReviewAddedEvent.builder()
                .reviewId(reviewEntity.getId())
                .userId(reviewEntity.getUserId())
                .placeId(reviewEntity.getPlaceId())
                .rating(reviewEntity.getRating())
                .content(reviewEntity.getContent())
                .build();

        kafkaProducer.sendReviewAddedEvent(event);

        return reviewDto;
    }

    @Override
    public ReviewDto getReviewByPlaceId(int placeId) {
        ReviewEntity reviewEntity = reviewRepository.findByPlaceId(placeId);

        if (reviewEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        ReviewDto reviewDto = mapper.map(reviewEntity, ReviewDto.class);

        return reviewDto;
    }

    @Override
    public ReviewDto getReviewByUserId(int userId) {
        ReviewEntity userEntity = reviewRepository.findByUserId(userId);

        if (userEntity == null)
            return null;

        ModelMapper mapper = new ModelMapper();
        ReviewDto reviewDto = mapper.map(userEntity, ReviewDto.class);

        return reviewDto;
    }
}
