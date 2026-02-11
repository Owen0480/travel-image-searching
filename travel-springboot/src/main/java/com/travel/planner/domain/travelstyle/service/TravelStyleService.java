package com.travel.planner.domain.travelstyle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.planner.domain.travelstyle.domain.TravelStyleDomain;
import com.travel.planner.domain.travelstyle.dto.TravelStyleAnalysisDto;
import com.travel.planner.domain.travelstyle.dto.TypeInfoDto;
import com.travel.planner.domain.travelstyle.entity.TravelStyleResult;
import com.travel.planner.domain.travelstyle.repository.TravelStyleResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TravelStyleService {

    private static final String FASTAPI_URL = "http://localhost:8000/api/v1/travel-style/analyze";

    private final RestTemplate restTemplate = new RestTemplate();
    private final TravelStyleResultRepository travelStyleResultRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TravelStyleService(TravelStyleResultRepository travelStyleResultRepository) {
        this.travelStyleResultRepository = travelStyleResultRepository;
    }

    public TravelStyleAnalysisDto analyze(List<String> interests) {
        if (interests == null || interests.size() != 3) {
            throw new IllegalArgumentException("관심사는 정확히 3개 선택해야 합니다.");
        }

        TravelStyleAnalysisDto result;
        try {
            result = analyzeByFastApi(interests);
        } catch (Exception e) {
            log.warn("FastAPI 호출 실패, 로컬 분석 사용: {}", e.getMessage());
            result = analyzeLocally(interests);
        }
        saveResult(result);
        return result;
    }

    private void saveResult(TravelStyleAnalysisDto dto) {
        try {
            String userInterestsJson = objectMapper.writeValueAsString(dto.getUserInterests() != null ? dto.getUserInterests() : List.of());
            String matched = dto.getMatchedType() != null ? dto.getMatchedType() : "복합형";
            TravelStyleResult entity = TravelStyleResult.builder()
                    .matchedType(matched)
                    .userInterests(userInterestsJson)
                    .description(null)
                    .user(null)  // 비로그인 시 null
                    .build();
            travelStyleResultRepository.save(entity);
            log.info("TravelStyleResult 저장: matchedType={}", entity.getMatchedType());
        } catch (Exception e) {
            log.warn("TravelStyleResult 저장 실패: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private TravelStyleAnalysisDto analyzeByFastApi(List<String> interests) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of("interests", interests);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                FASTAPI_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = response.getBody();
        if (body == null || !Boolean.TRUE.equals(body.get("success"))) {
            throw new RuntimeException("FastAPI 분석 실패");
        }

        Map<String, Object> analysis = (Map<String, Object>) body.get("analysis");
        if (analysis == null) {
            throw new RuntimeException("분석 결과 없음");
        }

        TypeInfoDto typeInfo = null;
        if (analysis.get("type_info") != null) {
            Map<String, Object> ti = (Map<String, Object>) analysis.get("type_info");
            List<TypeInfoDto.DestinationDto> dests = null;
            if (ti.get("destinations") != null) {
                List<Map<String, Object>> destList = (List<Map<String, Object>>) ti.get("destinations");
                dests = destList.stream()
                        .map(d -> new TypeInfoDto.DestinationDto((String) d.get("name"), (String) d.get("desc")))
                        .toList();
            }
            typeInfo = TypeInfoDto.builder()
                    .description((String) ti.get("description"))
                    .keywords(ti.get("keywords") != null ? (List<String>) ti.get("keywords") : List.of())
                    .destinations(dests)
                    .build();
        }

        return TravelStyleAnalysisDto.builder()
                .matchedType((String) analysis.get("matched_type"))
                .typeBreakdown(null)
                .confidence(analysis.get("confidence") != null ? ((Number) analysis.get("confidence")).intValue() : null)
                .reason((String) analysis.get("reason"))
                .secondaryType((String) analysis.get("secondary_type"))
                .typeInfo(typeInfo)
                .userInterests(analysis.get("user_interests") != null ? (List<String>) analysis.get("user_interests") : interests)
                .build();
    }

    private TravelStyleAnalysisDto analyzeLocally(List<String> interests) {
        Map<String, Long> typeCounts = interests.stream()
                .map(kw -> TravelStyleDomain.KEYWORD_TO_TYPE.getOrDefault(kw, "복합형"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        if (typeCounts.isEmpty()) {
            typeCounts = Map.of("복합형", (long) interests.size());
        }

        int n = interests.size();
        List<Map<String, Object>> typeBreakdown = typeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> Map.<String, Object>of(
                        "type", e.getKey(),
                        "percent", (int) Math.round(e.getValue() * 100.0 / n),
                        "count", e.getValue().intValue()
                ))
                .toList();

        // 퍼센트 높은 순으로 타입명만 이어붙이기 (숫자 없음)
        String compositeLabel = typeBreakdown.size() == 1 && (Integer) typeBreakdown.get(0).get("percent") == 100
                ? (String) typeBreakdown.get(0).get("type")
                : typeBreakdown.stream()
                        .map(b -> (String) b.get("type"))
                        .collect(Collectors.joining("·"));

        String typeKey = typeBreakdown.isEmpty() ? "복합형" : (String) typeBreakdown.get(0).get("type");
        TypeInfoDto base = TravelStyleDomain.TRAVEL_TYPES.getOrDefault(typeKey, TravelStyleDomain.TRAVEL_TYPES.get("복합형"));
        // 복합 타입 설명: "활동적인 여행, 맛집·음식, 휴식과 힐링를 골고루 즐기는 타입입니다."
        String desc = typeBreakdown.size() > 1
                ? typeBreakdown.stream()
                        .map(b -> TravelStyleDomain.TYPE_SHORT_DESC.getOrDefault((String) b.get("type"), (String) b.get("type")))
                        .collect(Collectors.joining(", ")) + "를 골고루 즐기는 타입입니다."
                : base.getDescription();

        TypeInfoDto typeInfo = TypeInfoDto.builder()
                .description(desc)
                .keywords(base.getKeywords())
                .destinations(List.of())
                .build();

        return TravelStyleAnalysisDto.builder()
                .matchedType(compositeLabel)
                .typeBreakdown(null)
                .confidence(85)
                .reason("선택한 관심사(" + String.join(", ", interests) + ")를 기반으로 타입별 비율을 분석했습니다. 추천 여행지는 AI로 생성됩니다. (FastAPI/Ollama 연결 후 다시 시도해 주세요)")
                .secondaryType(null)
                .typeInfo(typeInfo)
                .userInterests(interests)
                .build();
    }
}
