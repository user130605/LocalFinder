package com.example.place_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAddedEvent {
    private int reviewId;
    private int userId;
    private int placeId;
    private int rating;
    private String content;
}
