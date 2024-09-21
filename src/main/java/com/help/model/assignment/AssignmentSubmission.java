package com.help.model.assignment;

import com.help.model.BaseEntity;
import com.help.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssignmentSubmission extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private Person submitter;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    private LocalDateTime date;
    @OneToMany(mappedBy = "assignmentSubmission", cascade = CascadeType.ALL, fetch = LAZY, orphanRemoval = true)
    private List<AssignmentSubmissionFile> files;
}
