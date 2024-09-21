package com.help.dto.incoming.assignment;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GradeCreationDto {
    @NotNull
    private Double grade;
    @NotNull
    private Long personId;
}
