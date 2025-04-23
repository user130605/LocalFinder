package com.example.place_service.vo;

import com.example.place_service.jpa.Category;
import lombok.Data;

@Data
public class RequestPlaceUpdate {

    private String name;
    private String businessHours;
    private String address;
    private Category category;
    private String description;
    private String phone;
}
