package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByPerson(Person person);

    boolean existsByIdAndPerson(Long id, Person person);

    boolean existsByPersonIdAndCourseId(Long personId, Long courseId);
}
