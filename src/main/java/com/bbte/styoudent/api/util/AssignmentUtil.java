package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.service.AssignmentGradeService;
import com.bbte.styoudent.service.AssignmentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class AssignmentUtil {
    private final AssignmentService assignmentService;
    private final AssignmentGradeService assignmentGradeService;

    public AssignmentUtil(AssignmentService assignmentService, AssignmentGradeService assignmentGradeService) {
        this.assignmentService = assignmentService;
        this.assignmentGradeService = assignmentGradeService;
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
}
