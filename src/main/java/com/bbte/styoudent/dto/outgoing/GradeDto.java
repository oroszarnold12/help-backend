package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class GradeDto {
    private Double grade;
    private ThinPersonDto submitter;
    private AssignmentDto assignment;
}
