package com.travel.planner.domain.recommend.controller;

import com.travel.planner.domain.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 이미지 업로드 → Spring Boot → FastAPI 추천 API 중계
 * 요구사항: 프론트 → 스프링부트 → FastAPI
 */
@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "preference", required = false) String preference
    ) {
        Map<String, Object> result = recommendService.analyzeImage(file, preference);
        return ResponseEntity.ok(result);
    }
}
