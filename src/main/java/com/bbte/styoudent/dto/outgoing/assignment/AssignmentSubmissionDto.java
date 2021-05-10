package com.bbte.styoudent.dto.outgoing.assignment;

import com.bbte.styoudent.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentSubmissionDto {
    private Long id;
    private PersonDto submitter;
    private LocalDateTime date;
    private Double grade;
    private List<AssignmentSubmissionFileDto> files;
}
