package com.help.dto.outgoing.quiz;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.util.Collection;

@Data
public class QuizSubmissionDto {
    private PersonDto submitter;
    private Collection<AnswerSubmissionDto> answerSubmissions;
}
