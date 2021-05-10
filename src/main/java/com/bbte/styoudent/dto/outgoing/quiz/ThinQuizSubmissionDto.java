package com.bbte.styoudent.dto.outgoing.quiz;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.util.Collection;

@Data
public class ThinQuizSubmissionDto {
    private PersonDto submitter;
    private Collection<ThinAnswerSubmissionDto> answerSubmissions;
}
