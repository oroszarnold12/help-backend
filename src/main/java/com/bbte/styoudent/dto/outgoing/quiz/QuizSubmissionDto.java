package com.bbte.styoudent.dto.outgoing.quiz;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.util.Collection;

@Data
public class QuizSubmissionDto {
    private PersonDto submitter;
    private Collection<AnswerSubmissionDto> answerSubmissions;
}
