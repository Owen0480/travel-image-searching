package com.travel.planner.domain.travel.service;

import com.travel.planner.domain.travel.dto.TravelResponseDto;
import com.travel.planner.global.util.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final ExternalApiService externalApiService;

    @Value("${external.fastapi.url}")
    private String fastApiBaseUrl;

    public TravelResponseDto getTravelRecommendation(String message) {
        String fastApiUrl = fastApiBaseUrl + "/api/v1/travel/travel";
        Map<String, Object> requestBody = Map.of("message", message);

        return externalApiService.post(
                fastApiUrl,
                requestBody,
                new ParameterizedTypeReference<TravelResponseDto>() {}
        );
    }
}

