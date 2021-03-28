package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.List;

@Data
public class AssignmentGradeDto {
    private Long id;
    private Double grade;
    private ThinPersonDto submitter;
    private ThinAssignmentDto assignment;
    private List<AssignmentGradeCommentDto> comments;
}
