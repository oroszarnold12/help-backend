package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
}
