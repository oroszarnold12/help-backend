package com.bbte.styoudent.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Integer points;
    private String description;
}
