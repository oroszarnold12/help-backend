package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.service.AnnouncementService;
import com.bbte.styoudent.service.FirebaseMessagingService;
import com.bbte.styoudent.service.ServiceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnnouncementUtil {
    private final AnnouncementService announcementService;
    private final FirebaseMessagingService firebaseMessagingService;

    public AnnouncementUtil(AnnouncementService announcementService,
                            FirebaseMessagingService firebaseMessagingService) {
        this.announcementService = announcementService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    public void checkIfHasThisAnnouncement(Long courseId, Long announcementId) {
        try {
            if (!announcementService.checkIfExistsByCourseIdAndId(courseId, announcementId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no announcement with id: " + announcementId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check announcement!", se);
        }
    }

    private void sendNotification(Note note, Person recipient) {
        if (recipient.getNotificationToken() != null) {
            try {
                firebaseMessagingService.sendNotification(note, recipient.getNotificationToken());
            } catch (ServiceException serviceException) {
                throw new InternalServerException(
                        "Could not send announcement notification for student!", serviceException
                );
            }
        }
    }

    private void sendMultipleNotification(Note note, List<Person> recipients) {
        List<String> failedFor = new ArrayList<>();

        recipients.forEach(recipient -> {
            try {
                sendNotification(note, recipient);
            } catch (InternalServerException internalServerException) {
                failedFor.add(recipient.getFirstName() + recipient.getLastName());
            }
        });

        if (failedFor.size() > 0) {
            throw new InternalServerException(
                    "Could not send announcement notification for people: " + String.join(", ", failedFor)
            );
        }
    }

    public void createMultipleNotificationsOfAnnouncementCreation(Announcement announcement) {
        Course course = announcement.getCourse();
        String title = course.getName() + " announcement created!";
        String body = "Check out " + announcement.getName() + "!";

        Note note = createDataForAnnouncementNotification(announcement, course, title, body);

        List<Person> participants =
                course.getParticipations().stream().map(Participation::getPerson).collect(Collectors.toList());

        sendMultipleNotification(note, participants);
    }

    public void createMultipleNotificationsOfAnnouncementComment(AnnouncementComment announcementComment) {
        Announcement announcement = announcementComment.getAnnouncement();
        Course course = announcement.getCourse();
        String title = "New comment for: " + announcement.getName();
        String body = announcementComment.getCommenter().getFirstName() + " " +
                announcementComment.getCommenter().getLastName() +
                ": " + Jsoup.clean(announcementComment.getContent(), "", Whitelist.none(),
                new Document.OutputSettings().prettyPrint(false));

        Note note = createDataForAnnouncementNotification(announcement, course, title, body);

        List<Person> teachers = course.getParticipations()
                .stream().map(Participation::getPerson)
                .filter(person -> person.getRole().equals(Role.ROLE_TEACHER))
                .collect(Collectors.toList());

        List<Person> commenters = announcement.getAnnouncementComments()
                .stream().map(AnnouncementComment::getCommenter)
                .collect(Collectors.toList());

        for (Person person : commenters){
            if (!teachers.contains(person))
                teachers.add(person);
        }

        sendMultipleNotification(note, teachers);
    }

    private Note createDataForAnnouncementNotification(Announcement announcement, Course course, String title, String body) {
        Map<String, String> data = new HashMap<>();
        data.put("forAnnouncement", "true");
        data.put("courseId", course.getId().toString());
        data.put("announcementId", announcement.getId().toString());

        return new Note(title, body, data);
    }
}
