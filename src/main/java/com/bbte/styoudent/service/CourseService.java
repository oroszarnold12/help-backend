package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Person;

import java.util.List;

public interface CourseService {
    List<Course> getAll() throws ServiceException;

    Course getById(Long id) throws ServiceException;

    Course save(Course course) throws ServiceException;

    void delete(Long id) throws ServiceException;

    List<Course> getAllCoursesByPerson(Person person) throws ServiceException;

    Course getCourseByPerson(Person person, Long id) throws ServiceException;
}
