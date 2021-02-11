package com.bbte.styoudent.dto.incoming;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubmissionGradeDto {
    @NotNull
    private Double grade;
}
