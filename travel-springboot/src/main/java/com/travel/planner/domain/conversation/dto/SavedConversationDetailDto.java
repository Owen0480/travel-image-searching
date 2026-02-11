package com.travel.planner.domain.conversation.dto;

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
public class SavedConversationDetailDto {
    private Long id;
    private String title;
    private List<SaveConversationRequest.MessageItem> messages;
    private LocalDateTime createdAt;
}
