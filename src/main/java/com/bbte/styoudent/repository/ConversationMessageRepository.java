package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.conversation.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    void deleteConversationMessageById(Long conversationMessageId);

    Optional<ConversationMessage> findConversationMessageByConversationIdAndId(Long conversationId, Long id);
}
