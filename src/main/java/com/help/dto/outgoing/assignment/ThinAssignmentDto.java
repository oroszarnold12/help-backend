package com.help.dto.outgoing.assignment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThinAssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Integer points;
    private Boolean published;
}
