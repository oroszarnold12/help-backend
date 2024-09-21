package com.help.dto.incoming.assignment;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AssignmentCommentCreationDto {
    @NotEmpty
    @Size(max = 2048)
    private String content;
    @NotEmpty
    private String recipientEmail;
}
