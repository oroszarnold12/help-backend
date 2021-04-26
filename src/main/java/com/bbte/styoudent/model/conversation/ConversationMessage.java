package com.bbte.styoudent.model.conversation;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConversationMessage extends BaseEntity {
    @Column(length = 4096)
    private String content;
    private LocalDateTime creationDate;
    private Boolean deleted;
    @ManyToOne
    private Person creator;
    @ManyToOne
    private Conversation conversation;
}
