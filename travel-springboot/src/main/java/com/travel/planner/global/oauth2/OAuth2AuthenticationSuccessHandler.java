package com.travel.planner.global.oauth2;

import com.travel.planner.domain.auth.entity.RefreshToken;
import com.travel.planner.domain.auth.repository.RefreshTokenRepository;
import com.travel.planner.global.jwt.TokenProvider;
import com.travel.planner.global.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        String refreshToken = tokenProvider.createRefreshToken(authentication);
        refreshTokenRepository.save(new RefreshToken(authentication.getName(), refreshToken));

        CookieUtil.addCookie(response, "refreshToken", refreshToken, 1209600);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = tokenProvider.createAccessToken(authentication);

        // Frontend URL
        String targetUrl = "http://localhost:5173/auth/callback";

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();
    }
}
