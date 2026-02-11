package com.travel.planner.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelChatRequestDto {
    private String message;

    @JsonProperty("thread_id")
    private String threadId;
}
