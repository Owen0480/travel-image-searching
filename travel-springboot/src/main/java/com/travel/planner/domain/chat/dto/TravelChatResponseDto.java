package com.travel.planner.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelChatResponseDto {
    private String answer;

    @JsonProperty("thread_id")
    private String threadId;

    @JsonProperty("info_complete")
    private boolean infoComplete;
}
