package com.bbte.styoudent.repository.person;

import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Participation;
import com.bbte.styoudent.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    void deleteParticipationsByCourse(Course course);

    boolean existsByCourseIdAndPerson(Long courseId, Person person);

    List<Participation> findAllByPerson(Person person);

    Optional<Participation> findByCourseIdAndPerson(Long courseId, Person person);

    void deleteByPersonIdAndCourseId(Long personId, Long courseId);
}
