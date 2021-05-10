package com.bbte.styoudent.dto.incoming.quiz;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class QuizCreationDto {
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotEmpty
    @Size(max = 8192)
    private String description;
    @NotNull
    private LocalDateTime dueDate;
    @NotNull
    private LocalTime timeLimit;
    @NotNull
    private Boolean showCorrectAnswers;
    @NotNull
    private Boolean multipleAttempts;
    @NotNull
    private Boolean published;
}
