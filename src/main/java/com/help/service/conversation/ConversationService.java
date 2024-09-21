package com.help.service.conversation;

import com.help.model.conversation.Conversation;

import java.util.List;

public interface ConversationService {
    List<Conversation> getByPersonId(Long personId);

    Conversation getById(Long conversationId);

    Conversation save(Conversation conversation);
}
