package com.example.review_service.jpa;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int placeId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

}
