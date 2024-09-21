package com.help.service.impl.conversation;

import com.help.model.conversation.ConversationMessage;
import com.help.repository.conversation.ConversationMessageRepository;
import com.help.service.conversation.ConversationMessageService;
import com.help.service.ServiceException;
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
