package com.bbte.styoudent.dto.outgoing.conversation;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.util.List;

@Data
public class ThinConversationDto {
    private Long id;
    private String name;
    private List<PersonDto> participants;
    private PersonDto creator;
    private ConversationMessageDto lastMessage;
}
