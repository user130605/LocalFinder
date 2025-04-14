package com.example.review_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review-service")
public class ReviewController {

    @GetMapping("/welcome")
    public String welcome(){
        return "review-service 입니다.";
    }
}
