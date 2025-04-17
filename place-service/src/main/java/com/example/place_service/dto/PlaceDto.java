package com.example.place_service.dto;

import com.example.place_service.jpa.Category;
import lombok.Data;

@Data
public class PlaceDto {
    private int id;

    private String name;

    private String businessHours;

    private String address;

    private Category category;

    private String description;

    private int ownerId;

    private String phone;
}
