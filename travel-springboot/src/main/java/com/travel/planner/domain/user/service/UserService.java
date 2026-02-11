package com.travel.planner.domain.user.service;

import com.travel.planner.domain.user.dto.UserInfoResponse;
import com.travel.planner.domain.user.entity.User;
import com.travel.planner.domain.user.repository.UserRepository;
import com.travel.planner.global.exception.BusinessException;
import com.travel.planner.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        User user = userRepository.findByOauthIdentifier(authentication.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
}
