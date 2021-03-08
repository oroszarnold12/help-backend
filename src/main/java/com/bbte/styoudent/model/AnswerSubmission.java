package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answer_submission")
@Data
@ToString(callSuper = true, exclude = "quizSubmission")
@EqualsAndHashCode(callSuper = true)
public class AnswerSubmission extends BaseEntity {
    @ManyToOne
    private Answer answer;
    private Boolean picked;
    @ManyToOne
    private QuizSubmission quizSubmission;

}
