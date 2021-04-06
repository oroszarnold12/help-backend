package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssignmentSubmissionFile extends BaseEntity {
    @ManyToOne
    private AssignmentSubmission assignmentSubmission;
    private String fileName;
    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    private AssignmentSubmissionFileObject fileObject;
}
