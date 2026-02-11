package com.travel.planner.domain.user.service;

import com.travel.planner.domain.user.entity.User;
import com.travel.planner.domain.user.repository.UserRepository;
import com.travel.planner.global.exception.BusinessException;
import com.travel.planner.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 구글에서 유저 정보를 가져옴
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "소셜 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    private OidcUser processOAuth2User(OidcUserRequest userRequest, OidcUser oidcUser) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oidcUser.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 구글의 고유 ID(sub)
        String oauthId = oidcUser.getName();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // DB 저장 및 업데이트
        saveOrUpdate(email, name, oauthId, registrationId, accessToken);

        return oidcUser;
    }

    private User saveOrUpdate(String email, String name, String oauthId, String registrationId, String accessToken) {
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.toBuilder()
                        .fullName(name)
                        .oauthIdentifier(oauthId)
                        .socialAccessToken(accessToken)
                        .build())
                .orElse(User.builder()
                        .email(email)
                        .fullName(name)
                        .oauthProvider(User.OAuthProvider.valueOf(registrationId.toUpperCase()))
                        .oauthIdentifier(oauthId)
                        .socialAccessToken(accessToken)
                        .role(User.UserRole.ROLE_USER)
                        .status(User.UserStatus.ACTIVE)
                        .build());

        return userRepository.save(user);
    }
}
