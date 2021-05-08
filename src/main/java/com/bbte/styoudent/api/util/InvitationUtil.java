package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Note;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.InvitationService;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InvitationUtil {
    private final FirebaseUtil firebaseUtil;
    private final CourseService courseService;
    private final PersonService personService;
    private final InvitationService invitationService;

    public InvitationUtil(FirebaseUtil firebaseUtil, CourseService courseService, PersonService personService,
                          InvitationService invitationService) {
        this.firebaseUtil = firebaseUtil;
        this.courseService = courseService;
        this.personService = personService;
        this.invitationService = invitationService;
    }

    private Note createDataForInvitationNotification(String title, String body) {
        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("forInvitation", "true");

        return new Note(title, body, data);
    }

    public void createSingleNotificationForInvitation(Invitation invitation) {
        Course course = invitation.getCourse();
        String title = "New invitation";
        String body = "You have been invited to " + course.getName();

        Note note = createDataForInvitationNotification(title, body);

        firebaseUtil.sendNotification(note, invitation.getPerson(), "invitation");
    }

    public Course getCourse(Long courseId) {
        try {
            return courseService.getById(courseId);
        } catch (ServiceException se) {
            throw new BadRequestException(
                    "Course with id: " + courseId + " doesn't exists!", se
            );
        }
    }

    public List<Person> getPersons(String... emails) {
        List<Person> persons = new ArrayList<>();
        List<String> failedFor = new ArrayList<>();

        for (String email : emails) {
            try {
                persons.add(personService.getPersonByEmail(email));
            } catch (ServiceException se) {
                failedFor.add(email);
            }
        }

        if (!failedFor.isEmpty()) {
            throw new BadRequestException("Could not GET persons with emails: " + Arrays.toString(emails) + "!");
        }

        return persons;
    }

    public void checkIfExists(Long id, Person person) {
        try {
            if (!invitationService.checkIfExistsByIdAndPerson(id, person)) {
                throw new ForbiddenException("Access denied");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check if invitation exists!", se);
        }
    }
}
