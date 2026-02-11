package com.travel.planner.domain.travelstyle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 스타일 분석 API 응답")
public class TravelStyleResponseDto {

    @Schema(description = "성공 여부")
    private Boolean success;

    @Schema(description = "분석 결과")
    private TravelStyleAnalysisDto analysis;
}
