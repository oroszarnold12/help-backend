package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.Collection;

@Data
public class QuizSubmissionDto {
    private PersonDto submitter;
    private Collection<AnswerSubmissionDto> answerSubmissions;
}
