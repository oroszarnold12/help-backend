package com.help.dto.incoming.discussion;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class DiscussionCommentCreationDto {
    @NotEmpty
    @Size(max = 2048)
    private String content;
}
