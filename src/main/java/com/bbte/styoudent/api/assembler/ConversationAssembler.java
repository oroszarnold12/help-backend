package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.outgoing.conversation.ConversationDto;
import com.bbte.styoudent.dto.outgoing.conversation.ConversationMessageDto;
import com.bbte.styoudent.dto.outgoing.conversation.ThinConversationDto;
import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.model.conversation.ConversationMessage;
import com.bbte.styoudent.model.conversation.ConversationParticipation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConversationAssembler {
    private final ModelMapper modelMapper;
    private final PersonAssembler personAssembler;

    public ConversationAssembler(ModelMapper modelMapper, PersonAssembler personAssembler) {
        this.modelMapper = modelMapper;
        this.personAssembler = personAssembler;
    }

    public ThinConversationDto modelToThinDto(Conversation conversation) {
        sortMessages(conversation);

        ThinConversationDto thinConversationDto = modelMapper.map(conversation, ThinConversationDto.class);

        List<ConversationParticipation> participations = conversation.getConversationParticipations();

        if (participations != null) {
            thinConversationDto.setParticipants(participations.stream().map(conversationParticipation ->
                    personAssembler.modelToThinDto(conversationParticipation.getPerson()))
                    .collect(Collectors.toList()));
        }

        if (conversation.getMessages().size() > 0) {
            thinConversationDto.setLastMessage(messageModelToDto(
                    conversation.getMessages().get(conversation.getMessages().size() - 1))
            );
        }

        return thinConversationDto;
    }

    public ConversationDto modelToDto(Conversation conversation) {
        sortMessages(conversation);

        ConversationDto conversationDto = modelMapper.map(conversation, ConversationDto.class);

        List<ConversationParticipation> participations = conversation.getConversationParticipations();

        if (participations != null) {
            conversationDto.setParticipants(participations.stream().map(conversationParticipation ->
                    personAssembler.modelToThinDto(conversationParticipation.getPerson()))
                    .collect(Collectors.toList()));
        }

        return conversationDto;
    }

    public ConversationMessageDto messageModelToDto(ConversationMessage conversationMessage) {
        return modelMapper.map(conversationMessage, ConversationMessageDto.class);
    }

    private void sortMessages(Conversation conversation) {
        if (conversation.getMessages() != null) {
            conversation.getMessages().sort((conversationMessage1, conversationMessage2) -> {
                if (conversationMessage1.getCreationDate() == null || conversationMessage2.getCreationDate() == null) {
                    return 0;
                }

                return conversationMessage1.getCreationDate().compareTo(conversationMessage2.getCreationDate());
            });
        }
    }
}
