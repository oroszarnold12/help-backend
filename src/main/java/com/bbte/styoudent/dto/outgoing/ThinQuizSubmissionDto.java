package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.Collection;

@Data
public class ThinQuizSubmissionDto {
    private PersonDto submitter;
    private Collection<ThinAnswerSubmissionDto> answerSubmissions;
}
