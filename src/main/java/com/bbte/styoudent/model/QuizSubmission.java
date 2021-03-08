package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "quiz_submission")
@Data
@ToString(callSuper = true, exclude = "quiz")
@EqualsAndHashCode(callSuper = true)
public class QuizSubmission extends BaseEntity {
    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AnswerSubmission> answers;
    @ManyToOne
    private Quiz quiz;
    @ManyToOne
    private Person submitter;
}
