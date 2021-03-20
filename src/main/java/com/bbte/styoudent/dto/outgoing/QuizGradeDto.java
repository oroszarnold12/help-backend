package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class QuizGradeDto {
    private Double grade;
    private ThinPersonDto submitter;
    private ThinQuizDto quiz;
}