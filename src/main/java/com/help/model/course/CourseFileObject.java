package com.help.model.course;

import com.help.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CourseFileObject extends BaseEntity {
    @Lob
    private byte[] bytes;
}
