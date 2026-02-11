package com.travel.planner.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값이 올바르지 않습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "타입이 올바르지 않습니다"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_003", "필수 파라미터가 누락되었습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_004", "지원하지 않는 HTTP 메서드입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 내부 오류가 발생했습니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_006", "엔티티를 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_007", "접근 권한이 없습니다"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 사용중인 이메일입니다"),
    DUPLICATE_OAUTH_USER(HttpStatus.CONFLICT, "USER_003", "이미 가입된 OAuth 사용자입니다"),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER_004", "이미 삭제된 사용자입니다"),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "USER_005", "정지된 사용자입니다"),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "USER_006", "유효하지 않은 사용자 상태입니다"),

    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "만료된 토큰입니다"),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "지원하지 않는 토큰 형식입니다"),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH_004", "토큰 서명이 유효하지 않습니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_005", "인증 정보가 올바르지 않습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_006", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_007", "권한이 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_008", "리프레시 토큰을 찾을 수 없습니다"),
    INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH_009", "유효하지 않은 OAuth 제공자입니다"),

    // Resource
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_001", "리소스를 찾을 수 없습니다"),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "RESOURCE_002", "이미 존재하는 리소스입니다"),
    RESOURCE_DELETED(HttpStatus.GONE, "RESOURCE_003", "삭제된 리소스입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

