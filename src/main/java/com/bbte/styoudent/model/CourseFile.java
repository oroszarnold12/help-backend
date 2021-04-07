package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CourseFile extends BaseEntity {
    private String fileName;
    private LocalDateTime creationDate;
    private Long size;
    @ManyToOne
    private Person uploader;
    @ManyToOne
    private Course course;
    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    private CourseFileObject courseFileObject;
}
