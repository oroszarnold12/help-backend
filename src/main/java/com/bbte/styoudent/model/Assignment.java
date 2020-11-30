package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment")
@Data
@ToString(exclude = "course", callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Assignment extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "points")
    private Integer points;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
