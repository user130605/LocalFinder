package com.example.review_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ReviewController {

    @GetMapping("/review-service")
    public String welcome(){
        return "review-service 입니다.";
    }
}
