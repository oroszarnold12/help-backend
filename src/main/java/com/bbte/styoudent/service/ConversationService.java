package com.bbte.styoudent.service;

import com.bbte.styoudent.model.conversation.Conversation;

import java.util.List;

public interface ConversationService {
    List<Conversation> getByPersonId(Long personId);

    Conversation getById(Long conversationId);

    Conversation save(Conversation conversation);
}
