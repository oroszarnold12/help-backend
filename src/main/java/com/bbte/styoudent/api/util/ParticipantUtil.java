package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Note;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ParticipantUtil {
    private final PersonService personService;
    private final CourseService courseService;
    private final FirebaseUtil firebaseUtil;

    public ParticipantUtil(PersonService personService, CourseService courseService, FirebaseUtil firebaseUtil) {
        this.personService = personService;
        this.courseService = courseService;
        this.firebaseUtil = firebaseUtil;
    }

    public void createSingleNotificationOfKick(Long personId, Long courseId) {
        try {
            Person person = personService.getPersonById(personId);
            Course course = courseService.getById(courseId);

            String title = "Bad news!";
            String body = "You have been kicked out from " + course.getName() + "!";

            Note note = new Note(title, body, new HashMap<>());

            firebaseUtil.sendNotification(note, person, "kick");
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not send notification!", serviceException);
        }
    }
}
