package com.travel.planner.domain.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveConversationRequest {
    private String title; // optional, e.g. first message or "대화"
    private List<MessageItem> messages;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageItem {
        private String role; // "user" | "assistant"
        private String content;
    }
}
