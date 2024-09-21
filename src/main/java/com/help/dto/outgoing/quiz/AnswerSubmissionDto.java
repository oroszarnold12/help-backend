package com.help.dto.outgoing.quiz;

import lombok.Data;

@Data
public class AnswerSubmissionDto {
    private AnswerDto answer;
    private Boolean picked;
}
