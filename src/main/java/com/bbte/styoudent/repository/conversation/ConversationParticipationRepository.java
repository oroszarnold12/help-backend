package com.bbte.styoudent.repository.conversation;

import com.bbte.styoudent.model.conversation.ConversationParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationParticipationRepository extends JpaRepository<ConversationParticipation, Long> {
    boolean existsByConversationIdAndPersonId(Long conversationId, Long personId);

    void deleteByPersonId(Long personId);
}
