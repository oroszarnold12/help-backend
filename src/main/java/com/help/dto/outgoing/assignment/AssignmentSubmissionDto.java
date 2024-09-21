package com.help.dto.outgoing.assignment;

import com.help.dto.outgoing.person.PersonDto;
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
