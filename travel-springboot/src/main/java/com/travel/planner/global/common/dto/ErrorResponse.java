package com.travel.planner.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    private Boolean success;

    @Schema(description = "에러 코드", example = "INVALID_INPUT")
    private String code;

    @Schema(description = "에러 메시지", example = "입력값이 올바르지 않습니다")
    private String message;

    @Schema(description = "필드별 에러 목록")
    private List<FieldError> errors;

    @Schema(description = "에러 발생 시간", example = "2024-01-20T10:30:00")
    private LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(String code, String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "필드 에러 정보")
    public static class FieldError {

        @Schema(description = "필드명", example = "email")
        private String field;

        @Schema(description = "입력된 값", example = "invalid-email")
        private String value;

        @Schema(description = "에러 사유", example = "올바른 이메일 형식이 아닙니다")
        private String reason;
    }
}
