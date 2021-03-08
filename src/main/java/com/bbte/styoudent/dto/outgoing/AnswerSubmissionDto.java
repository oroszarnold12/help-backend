package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

@Data
public class AnswerSubmissionDto {
    private AnswerDto answer;
    private Boolean picked;
}
