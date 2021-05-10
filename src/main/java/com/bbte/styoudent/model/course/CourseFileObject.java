package com.bbte.styoudent.model.course;

import com.bbte.styoudent.model.BaseEntity;
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
