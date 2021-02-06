package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class DiscussionCreationDto {
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotEmpty
    @Size(max = 16384)
    private String content;
}
