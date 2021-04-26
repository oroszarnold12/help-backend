package com.bbte.styoudent.model.conversation;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Conversation extends BaseEntity {
    @Column(length = 32)
    private String name;
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationParticipation> conversationParticipations;
    @ManyToOne
    private Person creator;
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationMessage> messages;
}
