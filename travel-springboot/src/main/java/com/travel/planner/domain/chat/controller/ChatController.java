package com.travel.planner.domain.chat.controller;

import com.travel.planner.domain.chat.dto.TravelChatRequestDto;
import com.travel.planner.domain.chat.dto.TravelChatResponseDto;
import com.travel.planner.domain.chat.service.TravelChatService;
import com.travel.planner.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final TravelChatService travelChatService;

    @PostMapping("/travel")
    public Mono<ResponseEntity<BaseResponse<TravelChatResponseDto>>> chat(@RequestBody TravelChatRequestDto requestDto) {
        return travelChatService.sendTravelChat(requestDto.getMessage(), requestDto.getThreadId())
                .map(response -> ResponseEntity.ok(BaseResponse.success(response)));
    }
}
