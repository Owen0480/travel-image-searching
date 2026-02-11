package com.travel.planner.domain.travelstyle.domain;

import com.travel.planner.domain.travelstyle.dto.TypeInfoDto;

import java.util.*;

/**
 * backend-fastapi app/domain/travel_style.py 기준 통일
 */
public final class TravelStyleDomain {

    public static final Map<String, TypeInfoDto> TRAVEL_TYPES = Map.ofEntries(
            Map.entry("액티브형", TypeInfoDto.builder().description("체력 소모가 많은 활동적인 여행을 즐기는 타입")
                    .keywords(List.of("자전거", "등산", "수영", "서핑", "런닝", "트레킹")).destinations(List.of()).build()),
            Map.entry("힐링형", TypeInfoDto.builder().description("느긋하게 쉬면서 재충전하는 여행을 선호하는 타입")
                    .keywords(List.of("카페", "독서", "온천", "스파", "요가", "명상")).destinations(List.of()).build()),
            Map.entry("문화형", TypeInfoDto.builder().description("도시의 문화와 예술을 즐기는 여행을 선호하는 타입")
                    .keywords(List.of("쇼핑", "미술", "박물관", "공연", "전시", "영화")).destinations(List.of()).build()),
            Map.entry("자연형", TypeInfoDto.builder().description("자연 속에서 시간을 보내는 것을 좋아하는 타입")
                    .keywords(List.of("등산", "캠핑", "낚시", "사진", "별보기", "트레킹")).destinations(List.of()).build()),
            Map.entry("미식형", TypeInfoDto.builder().description("맛집 탐방과 음식 경험을 중시하는 타입")
                    .keywords(List.of("맛집", "요리", "와인", "디저트", "푸드투어", "카페")).destinations(List.of()).build()),
            Map.entry("모험형", TypeInfoDto.builder().description("새로운 경험과 도전을 즐기는 타입")
                    .keywords(List.of("번지점프", "패러글라이딩", "스쿠버다이빙", "서핑", "암벽등반", "오프로드")).destinations(List.of()).build()),
            Map.entry("감성형", TypeInfoDto.builder().description("분위기와 감성을 중시하는 여행을 선호하는 타입")
                    .keywords(List.of("카페", "사진", "일몰", "야경", "독서", "음악")).destinations(List.of()).build()),
            Map.entry("복합형", TypeInfoDto.builder().description("여러 성향이 골고루 섞여 있는 균형잡힌 타입")
                    .keywords(List.of()).destinations(List.of()).build())
    );

    public static final Map<String, String> KEYWORD_TO_TYPE = new HashMap<>();
    static {
        for (Map.Entry<String, TypeInfoDto> e : TRAVEL_TYPES.entrySet()) {
            for (String kw : e.getValue().getKeywords()) {
                KEYWORD_TO_TYPE.putIfAbsent(kw, e.getKey());
            }
        }
    }

    /** 타입별 짧은 설명 (복합 타입 설명용) */
    public static final Map<String, String> TYPE_SHORT_DESC = Map.of(
            "액티브형", "활동적인 여행",
            "힐링형", "휴식과 힐링",
            "문화형", "문화·예술",
            "자연형", "자연 감상",
            "미식형", "맛집·음식",
            "모험형", "도전과 모험",
            "감성형", "분위기와 감성",
            "복합형", "다양한 경험"
    );

    public static final Map<String, List<TypeInfoDto.DestinationDto>> FALLBACK_DESTINATIONS = Map.ofEntries(
            Map.entry("액티브형", List.of(new TypeInfoDto.DestinationDto("제주도", "한라산 등반, 해안 트레킹"), new TypeInfoDto.DestinationDto("양양", "서핑, 패러글라이딩"))),
            Map.entry("힐링형", List.of(new TypeInfoDto.DestinationDto("제주 서귀포", "힐링 카페, 자연 휴식"), new TypeInfoDto.DestinationDto("경주", "온천, 역사와 휴식"))),
            Map.entry("문화형", List.of(new TypeInfoDto.DestinationDto("서울", "미술관, 박물관, 쇼핑"), new TypeInfoDto.DestinationDto("전주", "한옥마을, 전통문화"))),
            Map.entry("자연형", List.of(new TypeInfoDto.DestinationDto("제주도", "오름 트레킹, 별보기"), new TypeInfoDto.DestinationDto("순천", "순천만 습지"))),
            Map.entry("미식형", List.of(new TypeInfoDto.DestinationDto("전주", "한식 맛집"), new TypeInfoDto.DestinationDto("부산", "해산물, 밀면"))),
            Map.entry("모험형", List.of(new TypeInfoDto.DestinationDto("양양", "패러글라이딩"), new TypeInfoDto.DestinationDto("제주", "스쿠버다이빙"))),
            Map.entry("감성형", List.of(new TypeInfoDto.DestinationDto("부산 감천", "감성 카페, 일몰"), new TypeInfoDto.DestinationDto("강릉", "해돋이, 커피거리"))),
            Map.entry("복합형", List.of(new TypeInfoDto.DestinationDto("제주도", "다양한 경험"), new TypeInfoDto.DestinationDto("부산", "문화·맛집·해변")))
    );

    private TravelStyleDomain() {}
}
