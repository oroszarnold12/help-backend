package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.service.AssignmentGradeService;
import com.bbte.styoudent.service.AssignmentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class AssignmentUtil {
    private final AssignmentService assignmentService;
    private final AssignmentGradeService assignmentGradeService;
    private final FirebaseUtil firebaseUtil;

    public AssignmentUtil(AssignmentService assignmentService, AssignmentGradeService assignmentGradeService,
                          FirebaseUtil firebaseUtil) {
        this.assignmentService = assignmentService;
        this.assignmentGradeService = assignmentGradeService;
        this.firebaseUtil = firebaseUtil;
    }

    public void checkIfHasThisAssignment(Long courseId, Long assignmentId) {
        try {
            if (!assignmentService.checkIfExistsByCourseIdAndId(courseId, assignmentId)) {
                throw new NotFoundException(
                        "Course with id: " + courseId + " has no assignment with id: " + assignmentId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check assignment!", se);
        }
    }

    public boolean checkIfGraded(Long assignmentId, Person submitter) {
        try {
            return assignmentGradeService.checkIfExistsByAssignmentIdAndSubmitter(assignmentId, submitter);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check grade!", se);
        }
    }

    public void checkIfPublished(Long courseId, Long assignmentId) {
        try {
            if (!assignmentService.getByCourseIdAndId(courseId, assignmentId).getPublished()) {
                throw new NotFoundException(
                        "Course with id: " + courseId + " has no assignment with id: " + assignmentId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check assignment!", se);
        }
    }

    public String getCorrectedFileName(List<AssignmentSubmission> submissions, String oldFileName) {
        List<AssignmentSubmissionFile> files = submissions.stream().map(AssignmentSubmission::getFiles)
                .flatMap(List::stream).collect(Collectors.toList());

        int i = 1;
        final AtomicReference<String> newFileName = new AtomicReference<>();
        newFileName.set(oldFileName);
        while (files.stream().anyMatch((file) -> file.getFileName().equals(newFileName.get()))) {
            int index = oldFileName.lastIndexOf('.');
            String toAppend = "-" + i;
            newFileName.set(oldFileName.substring(0, index) + toAppend + oldFileName.substring(index));
            i++;
        }

        return newFileName.get();
    }

    public void createSingleNotificationOfAssignmentGraded(AssignmentGrade assignmentGrade, Person submitter) {
        Assignment assignment = assignmentGrade.getAssignment();
        Course course = assignment.getCourse();
        String title = assignment.getCourse().getName() + " assignment graded!";
        String body = "Grade of " + assignment.getName() + ": " + assignmentGrade.getGrade() + " out of "
                + assignment.getPoints();

        Note note = createDataForAssignmentNotification(assignment, course, title, body);

        firebaseUtil.sendNotification(note, submitter, "assignment grade");
    }

    public void createMultipleNotificationsOfAssignmentCreation(Assignment assignment) {
        if (assignment.getPublished()) {
            Course course = assignment.getCourse();
            String title = course.getName() + " assignment created!";
            String body = "Check out " + assignment.getName() + "!";

            Note note = createDataForAssignmentNotification(assignment, course, title, body);

            List<Person> participants =
                    course.getParticipations().stream().map(Participation::getPerson).collect(Collectors.toList());

            firebaseUtil.sendMultipleNotification(note, participants, "assignment");
        }
    }

    public void createMultipleNotificationsOfAssignmentSubmissionCreation(Assignment assignment) {
        Course course = assignment.getCourse();
        String title = "New submission!";
        String body = "Graded " + assignment.getName() + " has a new submission!";

        Note note = createDataForAssignmentSubmissionNotification(assignment, course, title, body);

        List<Person> teachers = course.getParticipations()
                .stream().map(Participation::getPerson)
                .filter(person -> person.getRole().equals(Role.ROLE_TEACHER))
                .collect(Collectors.toList());

        firebaseUtil.sendMultipleNotification(note, teachers, "assignment submission");
    }

    public void createMultipleNotificationsOfAssignmentSubmissionComment(AssignmentComment assignmentComment) {
        Assignment assignment = assignmentComment.getAssignment();
        Course course = assignment.getCourse();
        String title = "New comment for: " + assignment.getName();
        String body = assignmentComment.getCommenter().getFirstName() + " "
                + assignmentComment.getCommenter().getLastName()
                + ": " + assignmentComment.getContent();

        Note note = createDataForAssignmentSubmissionNotification(assignment, course, title, body);

        List<Person> teachers = course.getParticipations()
                .stream().map(Participation::getPerson)
                .filter(person -> person.getRole().equals(Role.ROLE_TEACHER))
                .collect(Collectors.toList());

        firebaseUtil.sendMultipleNotification(note, teachers, "assignment submission comment");
    }

    public void createSingleNotificationOfAssignmentSubmissionComment(AssignmentComment assignmentComment,
                                                                      Person recipient) {
        Assignment assignment = assignmentComment.getAssignment();
        Course course = assignment.getCourse();
        String title = "New comment for " + assignment.getName();
        String body = recipient.getFirstName()
                + " " + recipient.getLastName()
                + ": " + assignmentComment.getContent();

        Note note = createDataForAssignmentNotification(assignment, course, title, body);

        firebaseUtil.sendNotification(note, recipient, "assignment submission comment");
    }

    private Note createDataForAssignmentSubmissionNotification(
            Assignment assignment, Course course, String title, String body
    ) {
        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("forAssignmentSubmission", "true");
        data.put("courseId", course.getId().toString());
        data.put("assignmentId", assignment.getId().toString());

        return new Note(title, body, data);
    }

    private Note createDataForAssignmentNotification(Assignment assignment, Course course, String title, String body) {
        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("forAssignment", "true");
        data.put("courseId", course.getId().toString());
        data.put("assignmentId", assignment.getId().toString());

        return new Note(title, body, data);
    }
}
