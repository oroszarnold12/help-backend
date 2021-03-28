package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_grade_comment")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssignmentGradeComment extends BaseEntity {
    @Column(length = 2048)
    private String content;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "commenter_id")
    private Person commenter;
    @ManyToOne
    @JoinColumn(name = "assignment_grade_id")
    private AssignmentGrade assignmentGrade;
}