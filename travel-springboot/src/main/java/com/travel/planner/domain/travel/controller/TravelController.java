package com.travel.planner.domain.travel.controller;

import com.travel.planner.domain.travel.dto.TravelResponseDto;
import com.travel.planner.domain.travel.service.TravelService;
import com.travel.planner.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    /**
     * travel-frontend → travel-backend-springboot → backend-fastapi 전체 플로우 테스트용 엔드포인트
     */
    @PostMapping("/test")
    public ResponseEntity<BaseResponse> test(@RequestBody Map<String, Object> body) {
        String message = body.getOrDefault("message", "테스트 메시지").toString();
        TravelResponseDto response = travelService.getTravelRecommendation(message);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}

