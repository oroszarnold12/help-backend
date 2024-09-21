package com.help.dto.incoming.quiz;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AnswerCreationDto {
    @NotEmpty
    @Size(max = 2048)
    private String content;
    @NotNull
    private Boolean correct;
}
