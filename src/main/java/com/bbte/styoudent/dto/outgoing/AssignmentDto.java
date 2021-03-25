package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime dueDate;
    private Integer points;
    private String description;
    private Boolean published;
}
