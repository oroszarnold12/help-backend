package com.help.repository.conversation;

import com.help.model.conversation.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    void deleteConversationMessageById(Long conversationMessageId);

    Optional<ConversationMessage> findConversationMessageByConversationIdAndId(Long conversationId, Long id);
}
