package com.travel.planner.domain.travelstyle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 타입 상세 정보")
public class TypeInfoDto {

    @Schema(description = "타입 설명")
    private String description;

    @Schema(description = "키워드 목록")
    private List<String> keywords;

    @Schema(description = "추천 여행지 목록")
    private List<DestinationDto> destinations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "추천 여행지")
    public static class DestinationDto {
        @Schema(description = "여행지명")
        private String name;
        @Schema(description = "간단 설명")
        private String desc;
    }
}
