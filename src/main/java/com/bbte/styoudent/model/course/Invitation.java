package com.bbte.styoudent.model.course;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "invitation")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Invitation extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;
}
