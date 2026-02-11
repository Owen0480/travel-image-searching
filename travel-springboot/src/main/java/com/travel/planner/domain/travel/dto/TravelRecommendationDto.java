package com.travel.planner.domain.travel.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelRecommendationDto {
    private String name;
    private String region;
    private List<String> theme;
    private String description;
    private String reason;
    private Double score;
    private List<String> who;
    private List<String> why;
}
