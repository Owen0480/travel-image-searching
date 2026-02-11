package com.travel.planner.domain.user.controller;

import com.travel.planner.domain.user.dto.UserInfoResponse;
import com.travel.planner.domain.user.service.UserService;
import com.travel.planner.global.common.dto.BaseResponse;
import com.travel.planner.global.exception.BusinessException;
import com.travel.planner.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "현재 로그인한 사용자 정보 조회", description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 반환합니다.")
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<UserInfoResponse>> getCurrentUser() {
        UserInfoResponse userInfo = userService.getCurrentUserInfo();
        return ResponseEntity.ok(BaseResponse.success(userInfo));
    }

    @Operation(summary = "성공 예제", description = "성공 예제")
    @GetMapping("/success")
    public ResponseEntity<BaseResponse> success(){
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(BaseResponse.success(response));
//        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "오류오류이렇게쓰세요");

        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Operation(summary = "실패 예제", description = "실패 예제")
    @GetMapping("/failed")
    public ResponseEntity<BaseResponse> failed(){
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "오류오류이렇게쓰세요");
    }
}