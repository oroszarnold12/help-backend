package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement")
@Data
@ToString(exclude = "course", callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "content", length = 16384)
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
