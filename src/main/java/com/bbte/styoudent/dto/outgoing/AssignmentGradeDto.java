package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class AssignmentGradeDto {
    private Long id;
    private Double grade;
    private ThinPersonDto submitter;
    private ThinAssignmentDto assignment;
}
