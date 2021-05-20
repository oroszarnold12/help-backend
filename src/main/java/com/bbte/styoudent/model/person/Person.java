package com.bbte.styoudent.model.person;

import com.bbte.styoudent.model.BaseEntity;
import com.bbte.styoudent.model.announcement.Announcement;
import com.bbte.styoudent.model.announcement.AnnouncementComment;
import com.bbte.styoudent.model.assignment.AssignmentComment;
import com.bbte.styoudent.model.assignment.AssignmentGrade;
import com.bbte.styoudent.model.assignment.AssignmentSubmission;
import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.model.conversation.ConversationMessage;
import com.bbte.styoudent.model.conversation.ConversationParticipation;
import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.course.CourseFile;
import com.bbte.styoudent.model.discussion.Discussion;
import com.bbte.styoudent.model.discussion.DiscussionComment;
import com.bbte.styoudent.model.quiz.QuizGrade;
import com.bbte.styoudent.model.quiz.QuizSubmission;
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
