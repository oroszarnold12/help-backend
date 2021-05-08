package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class QuestionCreationDto {
    @NotEmpty
    @Size(max = 2048)
    private String content;
    @NotNull
    @PositiveOrZero
    private Double points;
    @Valid
    @Size(min = 2)
    private List<AnswerCreationDto> answers;
}
