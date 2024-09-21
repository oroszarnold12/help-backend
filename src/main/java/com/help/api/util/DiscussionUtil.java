package com.help.api.util;

import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.model.course.Course;
import com.help.model.discussion.Discussion;
import com.help.model.discussion.DiscussionComment;
import com.help.model.notification.Note;
import com.help.model.person.Participation;
import com.help.model.person.Person;
import com.help.model.person.Role;
import com.help.service.discussion.DiscussionService;
import com.help.service.ServiceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DiscussionUtil {
    private final DiscussionService discussionService;
    private final FirebaseUtil firebaseUtil;

    public DiscussionUtil(DiscussionService discussionService, FirebaseUtil firebaseUtil) {
        this.discussionService = discussionService;
        this.firebaseUtil = firebaseUtil;
    }

    public void checkIfHasThisDiscussion(Long courseId, Long discussionId) {
        try {
            if (!discussionService.checkIfExistsByCourseIdAndId(courseId, discussionId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no discussion with id: " + discussionId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check discussion!", se);
        }
    }

    private Note createDataForDiscussionNotification(Discussion discussion, Course course, String title, String body) {
        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("forDiscussion", "true");
        data.put("courseId", course.getId().toString());
        data.put("discussionId", discussion.getId().toString());

        return new Note(title, body, data);
    }

    public void createMultipleNotificationsOfDiscussionCreation(Discussion discussion) {
        Course course = discussion.getCourse();
        String title = course.getName() + " discussion created!";
        String body = "Check out " + discussion.getName() + "!";

        Note note = createDataForDiscussionNotification(discussion, course, title, body);

        List<Person> participants =
                course.getParticipations().stream().map(Participation::getPerson).collect(Collectors.toList());

        firebaseUtil.sendMultipleNotification(note, participants, "discussion");
    }

    public void createMultipleNotificationsOfDiscussionComment(DiscussionComment discussionComment) {
        Discussion discussion = discussionComment.getDiscussion();
        Course course = discussion.getCourse();
        String title = "New comment for: " + discussion.getName() + "!";
        String body = discussionComment.getCommenter().getFirstName() + " "
                + discussionComment.getCommenter().getLastName()
                + ": " + Jsoup.clean(discussionComment.getContent(), "", Whitelist.none(),
                new Document.OutputSettings().prettyPrint(false));

        Note note = createDataForDiscussionNotification(discussion, course, title, body);

        List<Person> teachers = course.getParticipations()
                .stream().map(Participation::getPerson)
                .filter(person -> person.getRole().equals(Role.ROLE_TEACHER))
                .collect(Collectors.toList());

        List<Person> commenters = discussion.getDiscussionComments()
                .stream().map(DiscussionComment::getCommenter)
                .collect(Collectors.toList());

        List<Person> recipients = new ArrayList<>(teachers);

        for (Person commenter : commenters) {
            if (!recipients.contains(commenter)) {
                recipients.add(commenter);
            }
        }

        if (!recipients.contains(discussion.getCreator())) {
            recipients.add(discussion.getCreator());
        }

        firebaseUtil.sendMultipleNotification(note, recipients, "discussion comment");
    }
}
