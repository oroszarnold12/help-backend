package com.bbte.styoudent.dto.incoming.conversation;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ConversationMessageCreationDto {
    @NotEmpty
    @Size(max = 4096)
    private String content;
}
