package com.bbte.styoudent.dto.outgoing.conversation;

import com.bbte.styoudent.dto.outgoing.PersonDto;
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
