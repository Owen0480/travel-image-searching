package com.travel.planner.domain.travelstyle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 스타일 분석 요청")
public class InterestRequestDto {

    @Schema(description = "관심사 목록 (한국어 키워드 3개)", example = "[\"자전거\", \"등산\", \"카페\"]")
    @Size(min = 3, max = 3, message = "관심사는 정확히 3개 선택해야 합니다.")
    private List<String> interests;
}
