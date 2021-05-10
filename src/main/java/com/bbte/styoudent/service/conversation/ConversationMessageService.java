package com.bbte.styoudent.service.conversation;

import com.bbte.styoudent.model.conversation.ConversationMessage;

public interface ConversationMessageService {
    ConversationMessage save(ConversationMessage conversationMessage);

    void deleteById(Long conversationMessageId);

    ConversationMessage getByConversationIdAndId(Long conversationId, Long id);
}
