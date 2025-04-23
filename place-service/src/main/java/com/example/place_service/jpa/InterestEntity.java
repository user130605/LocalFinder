package com.example.place_service.jpa;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "interest")
@Data
public class InterestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private int placeId;
}

