package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import com.bbte.styoudent.model.Person;
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
