package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.model.conversation.ConversationParticipation;
import com.bbte.styoudent.repository.ConversationParticipationRepository;
import com.bbte.styoudent.service.ConversationParticipationService;
import com.bbte.styoudent.service.ServiceException;
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
