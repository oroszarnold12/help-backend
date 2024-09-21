package com.help.dto.incoming.conversation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class ConversationCreationDto {
    @Length(max = 32)
    private String name;
    @NotEmpty
    private String[] emails;
}
