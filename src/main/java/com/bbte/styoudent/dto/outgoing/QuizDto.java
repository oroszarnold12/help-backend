package com.bbte.styoudent.dto.outgoing;

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
}
