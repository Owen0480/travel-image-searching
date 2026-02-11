package com.travel.planner.domain.auth.controller;

import com.travel.planner.domain.auth.dto.LoginRequestDto;
import com.travel.planner.domain.auth.dto.RegisterRequestDto;
import com.travel.planner.domain.auth.dto.TokenResponseDto;
import com.travel.planner.domain.auth.service.AuthService;
import com.travel.planner.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final int REFRESH_TOKEN_MAX_AGE = 1209600; // 2 weeks

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authService.register(registerRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto);
        
        CookieUtil.addCookie(response, "refreshToken", tokenResponseDto.getRefreshToken(), REFRESH_TOKEN_MAX_AGE);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, "refreshToken")
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found in cookie"));

        TokenResponseDto tokenResponseDto = authService.refresh(refreshToken);

        CookieUtil.addCookie(response, "refreshToken", tokenResponseDto.getRefreshToken(), REFRESH_TOKEN_MAX_AGE);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        String authHeader = request.getHeader("Authorization");
        if (authentication != null && authentication.isAuthenticated() && authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.logout(authentication.getName(), accessToken);
        }
        
        CookieUtil.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String authHeader = request.getHeader("Authorization");
        if (authentication != null && authentication.isAuthenticated() && authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.withdrawByOauthIdentifier(authentication.getName(), accessToken);
        }

        CookieUtil.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok().build();
    }
}
