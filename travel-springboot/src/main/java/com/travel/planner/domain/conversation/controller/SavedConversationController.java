package com.travel.planner.domain.conversation.controller;

import com.travel.planner.domain.conversation.dto.SavedConversationDetailDto;
import com.travel.planner.domain.conversation.dto.SavedConversationListItem;
import com.travel.planner.domain.conversation.dto.SaveConversationRequest;
import com.travel.planner.domain.conversation.entity.SavedConversation;
import com.travel.planner.domain.conversation.service.SavedConversationService;
import com.travel.planner.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Conversation", description = "대화 저장 API")
@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class SavedConversationController {

    private final SavedConversationService savedConversationService;

    @Operation(summary = "대화 저장", description = "상단 별 클릭 시 현재 대화 내용을 DB에 저장")
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> save(@RequestBody(required = false) SaveConversationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
        //     return ResponseEntity.status(401).body(BaseResponse.fail("UNAUTHORIZED", "로그인이 필요합니다."));
        // }
        if (request == null) {
            return ResponseEntity.badRequest().body(BaseResponse.fail("INVALID_INPUT", "요청 본문이 없습니다."));
        }
        String userEmail = auth.getName();
        SavedConversation saved = savedConversationService.save(userEmail, request);
        return ResponseEntity.ok(BaseResponse.success(saved.getId(), "대화가 저장되었습니다."));
    }

    @Operation(summary = "저장된 대화 목록", description = "현재 사용자의 저장된 대화 목록 조회 (RECENT DISCOVERIES용)")
    @GetMapping
    public ResponseEntity<BaseResponse<List<SavedConversationListItem>>> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(BaseResponse.success(List.of()));
        }
        List<SavedConversationListItem> list = savedConversationService.getList(auth.getName());
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @Operation(summary = "저장된 대화 상세", description = "클릭한 대화 내용 조회 (채팅 화면에 표시용)")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SavedConversationDetailDto>> getById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).body(BaseResponse.fail("UNAUTHORIZED", "로그인이 필요합니다."));
        }
        SavedConversationDetailDto dto = savedConversationService.getById(id, auth.getName());
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
}
