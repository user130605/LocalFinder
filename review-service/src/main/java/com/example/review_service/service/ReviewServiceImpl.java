package com.example.review_service.service;

import com.example.review_service.config.KafkaProducer;
import com.example.review_service.dto.ReviewDto;
import com.example.review_service.event.ReviewAddedEvent;
import com.example.review_service.jpa.ReviewEntity;
import com.example.review_service.jpa.ReviewRepository;
import com.example.review_service.vo.RequestReviewUpdate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
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
    public List<ReviewDto> getReviewByPlaceId(int placeId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ReviewEntity> reviewEntities = reviewRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RuntimeException("해당 리뷰를 찾을 수 없습니다."));

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (ReviewEntity entity : reviewEntities) {
            ReviewDto dto = mapper.map(entity, ReviewDto.class);
            reviewDtos.add(dto);
        }

        return reviewDtos;
    }

    @Override
    public List<ReviewDto> getReviewByUserId(int userId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ReviewEntity> reviewEntities = reviewRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 리뷰를 찾을 수 없습니다."));

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (ReviewEntity entity : reviewEntities) {
            ReviewDto dto = mapper.map(entity, ReviewDto.class);
            reviewDtos.add(dto);
        }

        return reviewDtos;
    }

    @Override
    @Transactional
    public void updateReview(int reviewId, RequestReviewUpdate request) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        reviewEntity.setContent(request.getContent());
        reviewEntity.setRating(request.getRating());

        reviewRepository.save(reviewEntity);
    }

    @Override
    @Transactional
    public void deleteReview(int reviewId) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        reviewRepository.delete(reviewEntity);

    }
}