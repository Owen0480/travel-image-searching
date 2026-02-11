package com.travel.planner.domain.travelstyle.controller;

import com.travel.planner.domain.travelstyle.domain.TravelStyleDomain;
import com.travel.planner.domain.travelstyle.dto.InterestRequestDto;
import com.travel.planner.domain.travelstyle.dto.TravelStyleAnalysisDto;
import com.travel.planner.domain.travelstyle.dto.TravelStyleResponseDto;
import com.travel.planner.domain.travelstyle.service.TravelStyleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Travel Style", description = "여행 스타일 분석 API (backend-fastapi domain 기준 통일)")
@RestController
@RequestMapping("/api/v1/travel-style")
@RequiredArgsConstructor
public class TravelStyleController {

    private final TravelStyleService travelStyleService;

    @Operation(summary = "관심사 옵션 조회", description = "domain INTEREST_OPTIONS 반환")
    @GetMapping("/options")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getOptions() {
        List<Map<String, Object>> opts = TravelStyleDomain.TRAVEL_TYPES.entrySet().stream()
                .filter(e -> !e.getKey().equals("복합형"))
                .map(e -> Map.<String, Object>of("type", e.getKey(), "keywords", e.getValue().getKeywords()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("options", opts));
    }

    @Operation(summary = "여행 타입 분석", description = "관심사 3개(한국어 키워드)를 선택하여 여행 스타일을 분석합니다.")
    @PostMapping("/analyze")
    public ResponseEntity<TravelStyleResponseDto> analyze(@Valid @RequestBody InterestRequestDto request) {
        TravelStyleAnalysisDto analysis = travelStyleService.analyze(request.getInterests());

        TravelStyleResponseDto response = TravelStyleResponseDto.builder()
                .success(true)
                .analysis(analysis)
                .build();

        return ResponseEntity.ok(response);
    }
}
