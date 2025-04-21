package com.example.review_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceCreatedEvent {
    private int placeId;
    private int ownerId;
    private String name;
}
