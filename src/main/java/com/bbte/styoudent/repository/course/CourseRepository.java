package com.bbte.styoudent.repository.course;

import com.bbte.styoudent.model.course.Course;
import com.bbte.styoudent.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select distinct p.course from Participation p where p.person = :person")
    List<Course> findAllByPerson(@Param("person") Person person);

    @Query("select p.course from Participation p where p.person = :person and p.course.id = :id")
    Optional<Course> findByPerson(@Param("person") Person person, @Param("id") Long id);
}
