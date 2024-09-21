package com.help.model.quiz;

import com.help.model.BaseEntity;
import com.help.model.person.Person;
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
    private Collection<AnswerSubmission> answerSubmissions;
    @ManyToOne
    private Quiz quiz;
    @ManyToOne
    private Person submitter;
}
