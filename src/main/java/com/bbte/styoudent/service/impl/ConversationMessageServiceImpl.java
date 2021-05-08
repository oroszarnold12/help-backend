package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.conversation.ConversationMessage;
import com.bbte.styoudent.repository.ConversationMessageRepository;
import com.bbte.styoudent.service.ConversationMessageService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ConversationMessageServiceImpl implements ConversationMessageService {
    private final ConversationMessageRepository conversationMessageRepository;

    public ConversationMessageServiceImpl(ConversationMessageRepository conversationMessageRepository) {
        this.conversationMessageRepository = conversationMessageRepository;
    }

    @Override
    public ConversationMessage save(ConversationMessage conversationMessage) {
        try {
            return conversationMessageRepository.save(conversationMessage);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation message insertion failed!", dataAccessException);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long conversationMessageId) {
        try {
            conversationMessageRepository.deleteConversationMessageById(conversationMessageId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Conversation message deletion failed!", dataAccessException);
        }
    }

    @Override
    public ConversationMessage getByConversationIdAndId(Long conversationId, Long id) {
        return conversationMessageRepository.findConversationMessageByConversationIdAndId(conversationId, id)
                .orElseThrow(() -> new ServiceException("Conversation message selection failed!"));
    }
}
