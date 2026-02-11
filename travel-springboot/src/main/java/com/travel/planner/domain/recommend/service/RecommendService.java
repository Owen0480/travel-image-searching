package com.travel.planner.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 프론트 → 스프링부트 → FastAPI 이미지 추천 중계
 */
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestTemplate restTemplate;

    @Value("${external.fastapi.url}")
    private String fastApiBaseUrl;

    public Map<String, Object> analyzeImage(MultipartFile file, String preference) {
        String url = fastApiBaseUrl + "/api/v1/recommend/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("이미지 읽기 실패", e);
        }
        if (preference != null) {
            body.add("preference", preference);
        }

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }
}
