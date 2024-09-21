package com.help.service.impl.conversation;

import com.help.model.conversation.Conversation;
import com.help.repository.conversation.ConversationRepository;
import com.help.service.conversation.ConversationService;
import com.help.service.ServiceException;
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
