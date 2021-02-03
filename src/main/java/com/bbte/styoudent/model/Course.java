package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "course")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "description", length = 65536)
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Person teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discussion> discussions;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations;

    public void setAssignments(List<Assignment> assignments) {
        if (this.assignments == null) {
            this.assignments = assignments;
        } else {
            this.assignments.clear();
            this.assignments.addAll(assignments);
        }
    }

    public void setAnnouncements(List<Announcement> announcements) {
        if (this.announcements == null) {
            this.announcements = announcements;
        } else {
            this.announcements.clear();
            this.announcements.addAll(announcements);
        }
    }

    public void setDiscussions(List<Discussion> discussions) {
        if (this.discussions == null) {
            this.discussions = discussions;
        } else {
            this.discussions.clear();
            this.discussions.addAll(discussions);
        }
    }
}
