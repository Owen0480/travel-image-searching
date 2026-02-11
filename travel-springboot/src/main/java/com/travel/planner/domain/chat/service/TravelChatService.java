package com.travel.planner.domain.chat.service;

import com.travel.planner.domain.chat.dto.TravelChatRequestDto;
import com.travel.planner.domain.chat.dto.TravelChatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelChatService {

    private final WebClient webClient;

    @Value("${external.fastapi.url}")
    private String fastApiBaseUrl;

    /**
     * FastAPI의 /api/v1/travel-chat/travel/chat 엔드포인트로 요청을 보냅니다.
     * WebClient를 사용하여 비동기/논블로킹 방식으로 처리합니다.
     */
    public Mono<TravelChatResponseDto> sendTravelChat(String message, String threadId) {
        String url = fastApiBaseUrl + "/api/v1/travel-chat/travel/chat";
        
        TravelChatRequestDto requestDto = TravelChatRequestDto.builder()
                .message(message)
                .threadId(threadId)
                .build();

        log.info("Sending request to FastAPI: {} | message: {} | threadId: {}", url, message, threadId);

        return webClient.post()
                .uri(url)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("FastAPI Error Response: {} | Body: {}", response.statusCode(), errorBody);
                                return Mono.error(new RuntimeException("FastAPI 호출 실패: " + response.statusCode()));
                            });
                })
                .bodyToMono(TravelChatResponseDto.class)
                .doOnNext(response -> log.info("Received response from FastAPI: {}", response))
                .doOnError(error -> log.error("WebClient Error: {}", error.getMessage()));
    }
}
