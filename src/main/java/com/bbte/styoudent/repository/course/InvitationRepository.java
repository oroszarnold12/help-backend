package com.bbte.styoudent.repository.course;

import com.bbte.styoudent.model.course.Invitation;
import com.bbte.styoudent.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByPerson(Person person);

    boolean existsByIdAndPerson(Long id, Person person);

    boolean existsByPersonIdAndCourseId(Long personId, Long courseId);
}
