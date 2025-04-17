package com.example.place_service.vo;

import com.example.place_service.jpa.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestPlace {
    @NotNull(message = "Place name cannot be null")
    @Size(min = 2, message = "Place name not be less than two charactors")
    private String name;

    @NotNull(message = "Business hours cannot be null")
    @Size(min = 2, message = "Business hours not be less than two charactors")
    private String businessHours;

    @NotNull(message = "Address cannot be null")
    @Size(min = 2, message = "Address not be less than two charactors")
    private String address;

    @NotNull(message = "Category cannot be null")
    @Size(min = 2, message = "Category not be less than two charactors")
    private Category category;

    private String description;

    private int ownerId;

    @NotNull(message = "Phone number cannot be null")
    @Size(min = 2, message = "Phone number not be less than two charactors")
    private String phone;
}
