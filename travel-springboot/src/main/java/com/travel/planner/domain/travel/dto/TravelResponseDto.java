package com.travel.planner.domain.travel.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelResponseDto {
    private String response;
    private String intent;
    private String who;
    private String why;
    private Map<String, Object> constraints;
    private Map<String, Object> when_info;
    private String conversation_stage;
    private List<TravelRecommendationDto> recommendations;
    private String clarifying_question;
    private List<String> post_actions;
    private List<Map<String, Object>> favorite_items;
    private boolean needs_clarification;
    private Map<String, Object> filters;
    private String error;
}
