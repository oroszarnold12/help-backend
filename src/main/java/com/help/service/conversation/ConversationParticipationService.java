package com.help.service.conversation;

import com.help.model.person.Person;
import com.help.model.conversation.Conversation;

public interface ConversationParticipationService {
    boolean checkIfExistsByConversationIdAndPersonId(Long conversationId, Long personId);

    void createParticipation(Conversation conversation, Person person);

    void deleteParticipationByParticipantId(Long participantId);
}
