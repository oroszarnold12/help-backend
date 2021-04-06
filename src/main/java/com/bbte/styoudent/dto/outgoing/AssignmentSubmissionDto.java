package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentSubmissionDto {
    private Long id;
    private ThinPersonDto submitter;
    private LocalDateTime date;
    private Double grade;
    private List<AssignmentSubmissionFileDto> files;
}
