package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.Collection;

@Data
public class QuizSubmissionDto {
    private ThinPersonDto submitter;
    private Collection<AnswerSubmissionDto> answers;
}
