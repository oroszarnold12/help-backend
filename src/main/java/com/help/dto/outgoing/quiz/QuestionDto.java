package com.help.dto.outgoing.quiz;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private Long id;
    private String content;
    private Double points;
    private List<AnswerDto> answers;
}
