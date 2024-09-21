package com.help.model.assignment;

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
public class AssignmentSubmissionFileObject extends BaseEntity {
    @Lob
    private byte[] bytes;
}
