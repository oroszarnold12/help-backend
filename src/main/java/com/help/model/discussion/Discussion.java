package com.help.model.discussion;

import com.help.model.BaseEntity;
import com.help.model.course.Course;
import com.help.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discussion")
@Data
@ToString(exclude = "course", callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Discussion extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "content", length = 16384)
    private String content;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Person creator;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussionComment> discussionComments;
}
