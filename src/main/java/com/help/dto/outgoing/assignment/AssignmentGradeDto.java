package com.help.dto.outgoing.assignment;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

@Data
public class AssignmentGradeDto {
    private Long id;
    private Double grade;
    private PersonDto submitter;
    private ThinAssignmentDto assignment;
}
