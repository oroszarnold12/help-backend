package com.help.service.conversation;

import com.help.model.conversation.ConversationMessage;

public interface ConversationMessageService {
    ConversationMessage save(ConversationMessage conversationMessage);

    void deleteById(Long conversationMessageId);

    ConversationMessage getByConversationIdAndId(Long conversationId, Long id);
}
