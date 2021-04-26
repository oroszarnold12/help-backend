package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.conversation.Conversation;

public interface ConversationParticipationService {
    boolean checkIfExistsByConversationIdAndPersonId(Long conversationId, Long personId);

    void createParticipation(Conversation conversation, Person person);

    void deleteParticipationByParticipantId(Long participantId);
}
