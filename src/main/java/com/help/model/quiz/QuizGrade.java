package com.help.model.quiz;

import com.help.model.BaseEntity;
import com.help.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_grade")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QuizGrade extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private Person submitter;
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    private Double grade;
}

