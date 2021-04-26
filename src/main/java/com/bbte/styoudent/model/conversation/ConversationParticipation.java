package com.bbte.styoudent.model.conversation;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConversationParticipation extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Conversation conversation;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person person;
}
