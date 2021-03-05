package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "quiz")
@Data
@ToString(callSuper = true, exclude = "course")
@EqualsAndHashCode(callSuper = true)
public class Quiz extends BaseEntity {
    private String name;
    @Column(length = 8192)
    private String description;
    private LocalDateTime dueDate;
    private LocalTime timeLimit;
    private Double points;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
    @ManyToOne
    private Course course;
}
