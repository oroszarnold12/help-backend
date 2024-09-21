package com.help.dto.outgoing.quiz;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class QuizDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private LocalTime timeLimit;
    private Double points;
    private Boolean showCorrectAnswers;
    private Boolean multipleAttempts;
    private Boolean published;
}
