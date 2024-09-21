package com.help.service.impl.course;

import com.help.model.course.Course;
import com.help.model.person.Person;
import com.help.repository.course.CourseRepository;
import com.help.service.course.CourseService;
import com.help.service.person.ParticipationService;
import com.help.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final ParticipationService participationService;

    public CourseServiceImpl(CourseRepository courseRepository, ParticipationService participationService) {
        this.courseRepository = courseRepository;
        this.participationService = participationService;
    }

    @Override
    public List<Course> getAll() {
        try {
            return courseRepository.findAll();
        } catch (DataAccessException de) {
            throw new ServiceException("Course selection failed!");
        }
    }

    @Override
    public Course getById(Long id) {
        return courseRepository.findById(id).orElseThrow(
                () -> new ServiceException("Course selection with id: " + id + " failed!")
        );
    }

    @Override
    public Course save(Course course) {
        try {
            return courseRepository.save(course);
        } catch (DataAccessException de) {
            throw new ServiceException("Course insertion failed!", de);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            Course course = getById(id);
            participationService.deleteParticipationsByCourse(course);
            courseRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Course deletion with id " + id + " failed!", de);
        }
    }

    @Override
    public List<Course> getAllCoursesByPerson(Person person) {
        try {
            return courseRepository.findAllByPerson(person);
        } catch (DataAccessException de) {
            throw new ServiceException("Course selection failed!", de);
        }
    }

    @Override
    public Course getCourseByPerson(Person person, Long id) {
        return courseRepository.findByPerson(person, id).orElseThrow(
                () -> new ServiceException("Course selection with id " + id + " failed!"));
    }
}
