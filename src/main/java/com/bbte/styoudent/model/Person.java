package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "person")
@Data
@ToString(callSuper = true, exclude = "courses")
@EqualsAndHashCode(callSuper = true)
public class Person extends BaseEntity {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    private FileObject image;
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discussion> discussions;
    @OneToMany(mappedBy = "commenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementComment> announcementComments;
    @OneToMany(mappedBy = "commenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussionComment> discussionComments;
    @OneToMany(mappedBy = "commenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentComment> assignmentComments;
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentComment> recipientAssignmentComments;
    @OneToMany(mappedBy = "submitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentSubmission> assignmentSubmissions;
    @OneToMany(mappedBy = "submitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentGrade> assignmentGrades;
    @OneToMany(mappedBy = "submitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizSubmission> quizSubmissions;
    @OneToMany(mappedBy = "submitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizGrade> quizGrades;
}
