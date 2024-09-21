package com.help.model.person;

import com.help.model.BaseEntity;
import com.help.model.announcement.Announcement;
import com.help.model.announcement.AnnouncementComment;
import com.help.model.assignment.AssignmentComment;
import com.help.model.assignment.AssignmentGrade;
import com.help.model.assignment.AssignmentSubmission;
import com.help.model.conversation.Conversation;
import com.help.model.conversation.ConversationMessage;
import com.help.model.conversation.ConversationParticipation;
import com.help.model.course.Course;
import com.help.model.course.CourseFile;
import com.help.model.discussion.Discussion;
import com.help.model.discussion.DiscussionComment;
import com.help.model.quiz.QuizGrade;
import com.help.model.quiz.QuizSubmission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@SuppressWarnings("PMD.TooManyFields")
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
    private String personGroup;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    private String notificationToken;
    private Boolean sendNotifications;
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
    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseFile> courseFiles;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationMessage> conversationMessages;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversations;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationParticipation> conversationParticipations;
}
