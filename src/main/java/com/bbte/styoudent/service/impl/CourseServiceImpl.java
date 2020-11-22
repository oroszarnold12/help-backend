package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.repository.CourseRepository;
import com.bbte.styoudent.service.CourseService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Course getById(Long id) throws ServiceException {
        return courseRepository.findById(id).orElseThrow(
                () -> new ServiceException("Course selection with id: " + id + " failed")
        );
    }

    @Override
    public Course save(Course course) throws ServiceException {
        try {
            return courseRepository.save(course);
        } catch (DataAccessException de) {
            throw new ServiceException("Course insertion failed", de);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) throws ServiceException {
        try {
            courseRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Course deletion with id " + id + " failed", de);
        }
    }
}
