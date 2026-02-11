package com.travel.planner.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestTemplate restTemplate;

    /**
     * 공통 POST 요청 처리 (JSON 전용)
     */
    public <T, R> R post(String url, T body, ParameterizedTypeReference<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(body, headers);

        ResponseEntity<R> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                responseType
        );

        return response.getBody();
    }

    /**
     * 좀 더 범용적인 exchange 요청 처리
     */
    public <T, R> ResponseEntity<R> exchange(String url, HttpMethod method, HttpHeaders headers, T body, ParameterizedTypeReference<R> responseType) {
        if (headers == null) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        
        HttpEntity<T> entity = new HttpEntity<>(body, headers);
        
        return restTemplate.exchange(url, method, entity, responseType);
    }
}
