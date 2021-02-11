package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDto {
    private Long id;
    private ThinPersonDto submitter;
    private LocalDateTime date;
    private String fileName;
    private Double grade;
}
