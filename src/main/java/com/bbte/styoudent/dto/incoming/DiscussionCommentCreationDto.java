package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class DiscussionCommentCreationDto {
    @NotEmpty
    @Size(max = 2048)
    private String content;
}
