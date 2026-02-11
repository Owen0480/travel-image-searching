package com.travel.planner.domain.conversation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.planner.domain.conversation.dto.SavedConversationDetailDto;
import com.travel.planner.domain.conversation.dto.SavedConversationListItem;
import com.travel.planner.domain.conversation.dto.SaveConversationRequest;
import com.travel.planner.domain.conversation.entity.SavedConversation;
import com.travel.planner.domain.conversation.repository.SavedConversationRepository;
import com.travel.planner.domain.user.entity.User;
import com.travel.planner.domain.user.repository.UserRepository;
import com.travel.planner.global.exception.BusinessException;
import com.travel.planner.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SavedConversationService {

    private final SavedConversationRepository savedConversationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SavedConversation save(String userEmail, SaveConversationRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "저장할 메시지가 없습니다.");
        }
        User user = userRepository.findByEmail(userEmail)
                .orElseGet(() -> userRepository.findByOauthIdentifier(userEmail)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")));

        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            title = request.getMessages().isEmpty()
                    ? "대화"
                    : request.getMessages().get(0).getContent();
            if (title != null && title.length() > 200) title = title.substring(0, 200);
        }

        String messagesJson;
        try {
            messagesJson = objectMapper.writeValueAsString(request.getMessages());
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "메시지 저장 형식 오류");
        }

        SavedConversation saved = SavedConversation.builder()
                .user(user)
                .title(title)
                .messages(messagesJson)
                .build();
        return savedConversationRepository.save(saved);
    }

    @Transactional(readOnly = true)
    public List<SavedConversationListItem> getList(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseGet(() -> userRepository.findByOauthIdentifier(userEmail).orElse(null));
        if (user == null) {
            return List.of();
        }
        return savedConversationRepository.findAllByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(sc -> SavedConversationListItem.builder()
                        .id(sc.getId())
                        .title(sc.getTitle() != null ? sc.getTitle() : "대화")
                        .createdAt(sc.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SavedConversationDetailDto getById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseGet(() -> userRepository.findByOauthIdentifier(userEmail).orElse(null));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        SavedConversation sc = savedConversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "대화를 찾을 수 없습니다."));
        if (!sc.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 대화에 접근할 수 없습니다.");
        }
        List<SaveConversationRequest.MessageItem> messages;
        try {
            messages = objectMapper.readValue(sc.getMessages(), new TypeReference<List<SaveConversationRequest.MessageItem>>() {});
        } catch (JsonProcessingException e) {
            messages = List.of();
        }
        return SavedConversationDetailDto.builder()
                .id(sc.getId())
                .title(sc.getTitle())
                .messages(messages)
                .createdAt(sc.getCreatedAt())
                .build();
    }
}
