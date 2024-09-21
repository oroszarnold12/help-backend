package com.help.model.person;

import com.help.model.BaseEntity;
import com.help.model.course.Course;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "participation")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Participation extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;
    private Boolean showOnDashboard;
}
