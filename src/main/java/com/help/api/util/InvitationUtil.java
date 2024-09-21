package com.help.api.util;

import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.exception.InternalServerException;
import com.help.model.course.Course;
import com.help.model.course.Invitation;
import com.help.model.notification.Note;
import com.help.model.person.Person;
import com.help.service.course.CourseService;
import com.help.service.course.InvitationService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private List<Person> getPersonsByEmails(List<String> emails) {
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
            throw new BadRequestException("Could not find person(s) with e-mail(s): "
                    + String.join(", ", failedFor) + "!");
        }

        return persons;
    }

    private List<Person> getPersonsByGroups(List<String> personGroups) {
        try {
            return personService.getAllPersons()
                    .stream().filter(person -> personGroups.contains(person.getPersonGroup()))
                    .collect(Collectors.toList());
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not get persons!", serviceException);
        }
    }

    public List<Person> getPersons(boolean inviteByEmails, boolean inviteByPersonGroups,
                                   List<String> emails, List<String> personGroups) {
        List<Person> persons = new ArrayList<>();

        if (inviteByEmails) {
            if (emails == null || emails.isEmpty()) {
                throw new BadRequestException("List of emails should not be empty!");
            }

            persons = getPersonsByEmails(emails);
        }

        if (inviteByPersonGroups) {
            if (personGroups == null || personGroups.isEmpty()) {
                throw new BadRequestException("List of groups should not be empty!");
            }

            persons = getPersonsByGroups(personGroups);
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
