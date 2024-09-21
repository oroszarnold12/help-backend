package com.help.dto.incoming.assignment;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AssignmentSubmissionGradeDto {
    @NotNull
    private Double grade;
}
