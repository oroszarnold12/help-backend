package com.bbte.styoudent.model.assignment;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
}
