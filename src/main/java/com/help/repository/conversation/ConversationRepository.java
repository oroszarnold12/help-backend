package com.help.repository.conversation;

import com.help.model.conversation.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByCreatorId(Long creatorId);

    Optional<Conversation> findConversationById(Long conversationId);

    @Query("select distinct p.conversation from ConversationParticipation p where p.person.id = :personId")
    List<Conversation> findParticipantId(@Param("personId") Long personId);
}
