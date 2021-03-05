package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThinQuizDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Double points;
}
