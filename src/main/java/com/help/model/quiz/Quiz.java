package com.help.model.quiz;

import com.help.model.BaseEntity;
import com.help.model.course.Course;
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
    private Boolean showCorrectAnswers;
    private Boolean multipleAttempts;
    private Boolean published;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizSubmission> submissions;
    @ManyToOne
    private Course course;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizGrade> quizGrades;
}
