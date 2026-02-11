package com.travel.planner.domain.travelstyle.entity;

import com.travel.planner.domain.user.entity.User;
import com.travel.planner.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "travel_style_results", catalog = "travel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TravelStyleResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @Column(name = "matched_type", nullable = false, length = 100)
    private String matchedType;

    @Column(name = "user_interests", nullable = false, length = 500)
    private String userInterests; // JSON: ["맛집","전시","쇼핑"]

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // nullable - 비로그인 시
}
