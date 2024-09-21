package com.help.service.impl.conversation;

import com.help.model.person.Person;
import com.help.model.conversation.Conversation;
import com.help.model.conversation.ConversationParticipation;
import com.help.repository.conversation.ConversationParticipationRepository;
import com.help.service.conversation.ConversationParticipationService;
import com.help.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ConversationParticipationServiceImpl implements ConversationParticipationService {
    private final ConversationParticipationRepository convPartRepository;

    public ConversationParticipationServiceImpl(
            ConversationParticipationRepository convPartRepository
    ) {
        this.convPartRepository = convPartRepository;
    }

    @Override
    public boolean checkIfExistsByConversationIdAndPersonId(Long conversationId, Long personId) {
        try {
            return convPartRepository.existsByConversationIdAndPersonId(conversationId, personId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation participation checking failed!", dataAccessException);
        }
    }

    @Override
    public void createParticipation(Conversation conversation, Person person) {
        try {
            ConversationParticipation conversationParticipation = new ConversationParticipation();
            conversationParticipation.setConversation(conversation);
            conversationParticipation.setPerson(person);

            convPartRepository.save(conversationParticipation);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation participation insertion failed!", dataAccessException);
        }
    }

    @Transactional
    @Override
    public void deleteParticipationByParticipantId(Long participantId) {
        try {
            convPartRepository.deleteByPersonId(participantId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation participation deletion failed!", dataAccessException);
        }
    }
}
