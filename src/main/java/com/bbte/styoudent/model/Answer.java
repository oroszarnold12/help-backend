package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "answer")
@Data
@ToString(callSuper = true, exclude = "question")
@EqualsAndHashCode(callSuper = true)
public class Answer extends BaseEntity {
    @Column(length = 2048)
    private String content;
    private Boolean correct;
    @ManyToOne
    private Question question;
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerSubmission> answerSubmissions;
}
