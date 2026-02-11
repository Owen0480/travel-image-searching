package com.travel.planner.domain.conversation.entity;

import com.travel.planner.domain.user.entity.User;
import com.travel.planner.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_conversations", catalog = "travel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SavedConversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 200)
    private String title;

    @Lob
    @Column(name = "messages", nullable = false, columnDefinition = "TEXT")
    private String messages; // JSON: [{ "role": "user"|"assistant", "content": "..." }, ...]
}
