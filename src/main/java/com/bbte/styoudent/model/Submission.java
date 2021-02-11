package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Submission extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private Person submitter;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    private LocalDateTime date;
    private String fileName;
    private String uploadedFileName;
    private Double grade;
}
