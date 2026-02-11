package com.travel.planner.domain.auth.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 1209600)
public class RefreshToken {

    @Id
    private String email;

    private String token;

    public void updateToken(String token) {
        this.token = token;
    }
}
