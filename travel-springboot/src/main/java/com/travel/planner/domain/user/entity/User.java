package com.travel.planner.domain.user.entity;

import com.travel.planner.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", catalog = "travel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 20)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_identifier", unique = true, length = 255)
    private String oauthIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "social_access_token", length = 1000)
    private String socialAccessToken;

    public enum OAuthProvider {
        GOOGLE, KAKAO, NAVER, GITHUB, LOCAL
    }

    public enum UserRole {
        ROLE_USER, ROLE_ADMIN
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    // 비즈니스 메서드
    public void updateProfile(String fullName) {
        this.fullName = fullName;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
}