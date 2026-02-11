package com.travel.planner.domain.travelstyle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "여행 스타일 분석 결과")
public class TravelStyleAnalysisDto {

    @JsonProperty("matched_type")
    @Schema(description = "매칭된 여행 타입 (퍼센트 기반 복합)")
    private String matchedType;

    @JsonProperty("type_breakdown")
    @Schema(description = "타입별 퍼센트 [{type, percent, count}]")
    private List<Map<String, Object>> typeBreakdown;

    @Schema(description = "신뢰도 (%)")
    private Integer confidence;

    @Schema(description = "분석 이유")
    private String reason;

    @JsonProperty("secondary_type")
    @Schema(description = "두 번째 후보 타입")
    private String secondaryType;

    @JsonProperty("type_info")
    @Schema(description = "타입 상세 정보")
    private TypeInfoDto typeInfo;

    @JsonProperty("user_interests")
    @Schema(description = "사용자 선택 관심사")
    private List<String> userInterests;
}
