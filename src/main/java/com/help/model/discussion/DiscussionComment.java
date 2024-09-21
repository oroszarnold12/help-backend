package com.help.model.discussion;

import com.help.model.BaseEntity;
import com.help.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_comment")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DiscussionComment extends BaseEntity {
    @Column(length = 2048)
    private String content;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "commenter_id")
    private Person commenter;
    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;
}
