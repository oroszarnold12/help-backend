package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Integer points;
    private String description;
    private Boolean published;
    private List<AssignmentCommentDto> comments;
}
