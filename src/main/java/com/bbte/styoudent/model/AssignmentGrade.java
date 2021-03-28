package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "assignment_grade")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssignmentGrade extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private Person submitter;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    private Double grade;
    @OneToMany(mappedBy = "assignmentGrade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentGradeComment> assignmentGradeComments;
}
