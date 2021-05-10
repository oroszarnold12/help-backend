package com.bbte.styoudent.dto.outgoing.quiz;

import lombok.Data;

@Data
public class AnswerDto {
    private Long id;
    private String content;
    private Boolean correct;
}
