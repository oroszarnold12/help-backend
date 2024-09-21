package com.help.dto.outgoing.conversation;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationMessageDto {
    private Long id;
    private String content;
    private LocalDateTime creationDate;
    private PersonDto creator;
    private Boolean deleted;
}
