package com.bbte.styoudent.model.person;

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
public class FileObject extends BaseEntity {
    @Lob
    private byte[] bytes;
}
