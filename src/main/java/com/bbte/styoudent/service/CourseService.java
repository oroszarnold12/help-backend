package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface CourseService {
    List<Course> getAll();

    Course getById(Long id);

    Course save(Course course);

    void delete(Long id);

    List<Course> getAllCoursesByPerson(Person person);

    Course getCourseByPerson(Person person, Long id);
}
