package com.bbte.styoudent.service.course;

import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.course.Invitation;
import com.bbte.styoudent.model.person.Person;

import java.util.List;
import java.util.Optional;

public interface InvitationService {
    Invitation createInvitation(Course course, Person person);

    List<Invitation> getAllByPerson(Person person);

    boolean checkIfExistsByIdAndPerson(Long id, Person person);

    void deleteInvitation(Long id);

    Optional<Invitation> getInvitationById(Long id);

    boolean checkIfExistsByPersonIdAndCourseId(Long personId, Long courseId);
}
