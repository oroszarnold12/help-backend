package com.help.dto.outgoing.quiz;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThinQuizDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Double points;
    private Boolean published;
}
