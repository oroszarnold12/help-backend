package com.bbte.styoudent.dto.incoming.quiz;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QuizSubmissionCreationDto {
    @NotNull
    private Long answerId;
    @NotNull
    private Boolean picked;
}
