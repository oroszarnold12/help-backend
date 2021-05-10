package com.bbte.styoudent.model.assignment;

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
public class AssignmentSubmissionFileObject extends BaseEntity {
    @Lob
    private byte[] bytes;
}
