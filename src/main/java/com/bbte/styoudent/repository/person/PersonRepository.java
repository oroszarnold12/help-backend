package com.bbte.styoudent.repository.person;

import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select distinct p.person from Participation p where p.course = :course")
    List<Person> findByCoursesContains(Course course);
}
