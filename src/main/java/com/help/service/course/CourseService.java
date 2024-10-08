package com.help.service.course;

import com.help.model.course.Course;
import com.help.model.person.Person;

import java.util.List;

public interface CourseService {
    List<Course> getAll();

    Course getById(Long id);

    Course save(Course course);

    void delete(Long id);

    List<Course> getAllCoursesByPerson(Person person);

    Course getCourseByPerson(Person person, Long id);
}
