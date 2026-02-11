package com.travel.planner.domain.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedConversationListItem {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
