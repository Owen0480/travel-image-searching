package com.travel.planner.domain.conversation.repository;

import com.travel.planner.domain.conversation.entity.SavedConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedConversationRepository extends JpaRepository<SavedConversation, Long> {
    List<SavedConversation> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
