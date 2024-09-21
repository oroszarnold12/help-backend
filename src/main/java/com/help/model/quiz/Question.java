package com.help.model.quiz;

import com.help.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@ToString(callSuper = true, exclude = "quiz")
@EqualsAndHashCode(callSuper = true)
public class Question extends BaseEntity {
    @Column(length = 2048)
    private String content;
    private Double points;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;
    @ManyToOne
    private Quiz quiz;
}
