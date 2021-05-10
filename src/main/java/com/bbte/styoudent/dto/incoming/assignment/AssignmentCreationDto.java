package com.bbte.styoudent.dto.incoming.assignment;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class AssignmentCreationDto {
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotNull
    private LocalDateTime dueDate;
    @NotNull
    private Integer points;
    @NotEmpty
    @Size(max = 16384)
    private String description;
    @NotNull
    private Boolean published;
}
