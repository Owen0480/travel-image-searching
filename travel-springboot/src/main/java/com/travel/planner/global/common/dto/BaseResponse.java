package com.travel.planner.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 응답")
public class BaseResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "응답 메시지", example = "요청이 성공했습니다")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "에러 정보")
    private ErrorInfo error;

    @Schema(description = "응답 시간", example = "2024-01-20T10:30:00")
    private LocalDateTime timestamp;

    // 성공 응답 (데이터 있음)
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(
                true,
                "요청이 성공했습니다",
                data,
                null,
                LocalDateTime.now()
        );
    }

    // 성공 응답 (데이터 있음 + 커스텀 메시지)
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(
                true,
                message,
                data,
                null,
                LocalDateTime.now()
        );
    }

    // 성공 응답 (데이터 없음)
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(
                true,
                "요청이 성공했습니다",
                null,
                null,
                LocalDateTime.now()
        );
    }

    // 성공 응답 (데이터 없음 + 커스텀 메시지)
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(
                true,
                message,
                null,
                null,
                LocalDateTime.now()
        );
    }

    // 실패 응답
    public static <T> BaseResponse<T> fail(String code, String message) {
        return new BaseResponse<>(
                false,
                message,
                null,
                new ErrorInfo(code, message),
                LocalDateTime.now()
        );
    }

    // 실패 응답 (ErrorCode Enum 사용)
    public static <T> BaseResponse<T> fail(String code, String message, String details) {
        return new BaseResponse<>(
                false,
                message,
                null,
                new ErrorInfo(code, message, details),
                LocalDateTime.now()
        );
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "에러 정보")
    public static class ErrorInfo {

        @Schema(description = "에러 코드", example = "USER_NOT_FOUND")
        private String code;

        @Schema(description = "에러 메시지", example = "사용자를 찾을 수 없습니다")
        private String message;

        @Schema(description = "에러 상세 정보", example = "userId: 123")
        private String details;

        public ErrorInfo(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
