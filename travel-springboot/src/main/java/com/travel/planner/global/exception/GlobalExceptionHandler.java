package com.travel.planner.global.exception;

import com.travel.planner.global.common.dto.BaseResponse;
import com.travel.planner.global.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        BaseResponse<Void> response = e.getDetailMessage() != null
                ? BaseResponse.fail(errorCode.getCode(), errorCode.getMessage(), e.getDetailMessage())
                : BaseResponse.fail(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    /**
     * CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<BaseResponse<Void>> handleCustomException(CustomException e) {
        log.error("CustomException: code={}, message={}", e.getErrorCode(), e.getMessage());

        BaseResponse<Void> response = BaseResponse.fail(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null)
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE.getCode(),
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                fieldErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_TYPE_VALUE.getCode(),
                ErrorCode.INVALID_TYPE_VALUE.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 요청 본문(JSON) 파싱 실패 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        BaseResponse<Void> response = BaseResponse.fail(
                ErrorCode.INVALID_INPUT_VALUE.getCode(),
                "요청 본문 형식이 올바르지 않습니다."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 필수 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.MISSING_REQUEST_PARAMETER.getCode(),
                ErrorCode.MISSING_REQUEST_PARAMETER.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    /**
     * 핸들러를 찾을 수 없는 예외 처리 (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.ENTITY_NOT_FOUND.getCode(),
                "요청한 리소스를 찾을 수 없습니다"
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * Spring Security 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<BaseResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException: {}", e.getMessage());

        BaseResponse<Void> response = BaseResponse.fail(
                ErrorCode.UNAUTHORIZED.getCode(),
                ErrorCode.UNAUTHORIZED.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Spring Security 권한 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage());

        BaseResponse<Void> response = BaseResponse.fail(
                ErrorCode.ACCESS_DENIED.getCode(),
                ErrorCode.ACCESS_DENIED.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: ", e);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
