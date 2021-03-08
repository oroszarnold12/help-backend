package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.Collection;

@Data
public class ThinQuizSubmissionDto {
    private ThinPersonDto submitter;
    private Collection<ThinAnswerSubmissionDto> answers;
}
