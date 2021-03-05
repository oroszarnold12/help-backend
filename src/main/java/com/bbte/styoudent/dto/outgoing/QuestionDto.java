package com.bbte.styoudent.dto.outgoing;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private Long id;
    private String content;
    private Double points;
    private List<AnswerDto> answers;
}
