package com.help.dto.outgoing.quiz;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

@Data
public class QuizGradeDto {
    private Double grade;
    private PersonDto submitter;
    private ThinQuizDto quiz;
}