package com.help.model.announcement;

import com.help.model.BaseEntity;
import com.help.model.person.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_comment")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnnouncementComment extends BaseEntity {
    @Column(length = 2048)
    private String content;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "commenter_id")
    private Person commenter;
    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;
}
