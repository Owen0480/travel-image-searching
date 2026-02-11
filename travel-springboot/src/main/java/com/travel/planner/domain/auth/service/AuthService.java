package com.travel.planner.domain.auth.service;

import com.travel.planner.domain.auth.dto.LoginRequestDto;
import com.travel.planner.domain.auth.dto.RegisterRequestDto;
import com.travel.planner.domain.auth.dto.TokenResponseDto;
import com.travel.planner.domain.auth.entity.RefreshToken;
import com.travel.planner.domain.auth.repository.RefreshTokenRepository;
import com.travel.planner.domain.user.entity.User;
import com.travel.planner.domain.user.repository.UserRepository;
import com.travel.planner.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .fullName(registerRequestDto.getFullName())
                .oauthProvider(User.OAuthProvider.LOCAL)
                .role(User.UserRole.ROLE_USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        // 1. Authenticate user
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 2. Generate Tokens
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        // 3. Store Refresh Token in Redis
        RefreshToken refreshTokenEntity = new RefreshToken(authentication.getName(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(authentication.getName())
                .build();
    }

    @Transactional
    public TokenResponseDto refresh(String refreshToken) {
        // 1. Validate Refresh Token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token is invalid");
        }

        // 2. Get Authentication from Token
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 3. Check if Refresh Token exists in Redis
        RefreshToken savedToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not logged in"));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("Refresh Token does not match");
        }

        // 4. Generate New Tokens
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);

        // 5. Update Refresh Token in Redis
        savedToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .email(authentication.getName())
                .build();
    }

    @Transactional
    public void logout(String name, String accessToken) {
        // 1. Refresh Token 삭제 (Redis)
        refreshTokenRepository.deleteById(name);

        // 2. Access Token 블랙리스트 등록 (남은 유효시간만큼) - 서버 측에서의 무효화("삭제")
        if (accessToken != null) {
            Long expiration = tokenProvider.getExpiration(accessToken);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(accessToken, "logout", expiration, java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        }
    }

    @Transactional
    public void withdrawByOauthIdentifier(String name, String accessToken) {
        // 1. 토큰 및 세션 정리 (Access Token 블랙리스트 및 Refresh Token 삭제 로직 호출)
        logout(name, accessToken);

        // 2. 사용자 조회
        User user = userRepository.findByEmail(name)
                .orElseGet(() -> userRepository.findByOauthIdentifier(name)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        // 3. 소셜 계정인 경우 연동 해제 요청 (Google)
        if (user.getOauthProvider() == User.OAuthProvider.GOOGLE && user.getSocialAccessToken() != null) {
            revokeGoogleToken(user.getSocialAccessToken());
        }

        // 4. DB에서 사용자 삭제
        userRepository.delete(user);
    }

    private void revokeGoogleToken(String token) {
        try {
            String url = "https://oauth2.googleapis.com/revoke?token=" + token;
            restTemplate.postForObject(url, null, String.class);
            log.info("Google OAuth2 token revoked successfully.");
        } catch (Exception e) {
            log.warn("Failed to revoke Google OAuth2 token: {}", e.getMessage());
            // 토큰이 이미 만료되었거나 다른 이슈로 실패할 수 있으나, 회원 탈퇴 자체는 진행함
        }
    }
}