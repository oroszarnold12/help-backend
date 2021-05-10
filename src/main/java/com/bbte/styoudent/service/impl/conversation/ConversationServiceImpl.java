package com.bbte.styoudent.service.impl.conversation;

import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.repository.conversation.ConversationRepository;
import com.bbte.styoudent.service.conversation.ConversationService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationServiceImpl(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public List<Conversation> getByPersonId(Long personId) {
        try {
            return conversationRepository.findParticipantId(personId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation selection failed!", dataAccessException);
        }
    }

    @Override
    public Conversation getById(Long conversationId) {
        return conversationRepository.findConversationById(conversationId).orElseThrow(() ->
                new ServiceException("Conversation selection with id: " + conversationId + " failed!")
        );
    }

    @Override
    public Conversation save(Conversation conversation) {
        try {
            return conversationRepository.save(conversation);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation insertion failed!", dataAccessException);
        }
    }
}
