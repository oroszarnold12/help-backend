package com.bbte.styoudent.dto.outgoing.conversation;

import com.bbte.styoudent.dto.outgoing.ThinPersonDto;
import lombok.Data;

import java.util.List;

@Data
public class ConversationDto {
    private Long id;
    private String name;
    private List<ThinPersonDto> participants;
    private ThinPersonDto creator;
    private List<ConversationMessageDto> messages;
}
