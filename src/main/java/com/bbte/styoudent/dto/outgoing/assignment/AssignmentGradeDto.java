package com.bbte.styoudent.dto.outgoing.assignment;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

@Data
public class AssignmentGradeDto {
    private Long id;
    private Double grade;
    private PersonDto submitter;
    private ThinAssignmentDto assignment;
}
