package com.help.dto.outgoing.quiz;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.util.Collection;

@Data
public class ThinQuizSubmissionDto {
    private PersonDto submitter;
    private Collection<ThinAnswerSubmissionDto> answerSubmissions;
}
