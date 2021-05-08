package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.service.AnnouncementService;
import com.bbte.styoudent.service.ServiceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class AnnouncementUtil {
    private final AnnouncementService announcementService;
    private final FirebaseUtil firebaseUtil;

    public AnnouncementUtil(AnnouncementService announcementService, FirebaseUtil firebaseUtil) {
        this.announcementService = announcementService;
        this.firebaseUtil = firebaseUtil;
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

    public void createMultipleNotificationsOfAnnouncementCreation(Announcement announcement) {
        Course course = announcement.getCourse();
        String title = course.getName() + " announcement created!";
        String body = "Check out " + announcement.getName() + "!";

        Note note = createDataForAnnouncementNotification(announcement, course, title, body);

        List<Person> participants =
                course.getParticipations().stream().map(Participation::getPerson).collect(Collectors.toList());

        firebaseUtil.sendMultipleNotification(note, participants, "announcement");
    }

    public void createMultipleNotificationsOfAnnouncementComment(AnnouncementComment announcementComment) {
        Announcement announcement = announcementComment.getAnnouncement();
        Course course = announcement.getCourse();
        String title = "New comment for: " + announcement.getName();
        String body = announcementComment.getCommenter().getFirstName() + " "
                + announcementComment.getCommenter().getLastName()
                + ": " + Jsoup.clean(announcementComment.getContent(), "", Whitelist.none(),
                new Document.OutputSettings().prettyPrint(false));

        Note note = createDataForAnnouncementNotification(announcement, course, title, body);

        List<Person> teachers = course.getParticipations()
                .stream().map(Participation::getPerson)
                .filter(person -> person.getRole().equals(Role.ROLE_TEACHER))
                .collect(Collectors.toList());

        List<Person> commenters = announcement.getAnnouncementComments()
                .stream().map(AnnouncementComment::getCommenter)
                .collect(Collectors.toList());

        for (Person person : commenters) {
            if (!teachers.contains(person)) {
                teachers.add(person);
            }
        }

        firebaseUtil.sendMultipleNotification(note, teachers, "announcement comment");
    }

    private Note createDataForAnnouncementNotification(
            Announcement announcement, Course course, String title, String body
    ) {
        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("forAnnouncement", "true");
        data.put("courseId", course.getId().toString());
        data.put("announcementId", announcement.getId().toString());

        return new Note(title, body, data);
    }
}
