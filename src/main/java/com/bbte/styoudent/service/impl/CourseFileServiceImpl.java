package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.CourseFile;
import com.bbte.styoudent.repository.CourseFileRepository;
import com.bbte.styoudent.service.CourseFileService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseFileServiceImpl implements CourseFileService {
    private final CourseFileRepository courseFileRepository;

    public CourseFileServiceImpl(CourseFileRepository courseFileRepository) {
        this.courseFileRepository = courseFileRepository;
    }

    @Override
    public CourseFile save(CourseFile courseFile) {
        try {
            return courseFileRepository.save(courseFile);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Course file insertion failed!", dataAccessException);
        }
    }

    @Override
    public CourseFile getByCourseIdAndId(Long courseId, Long id) {
        return courseFileRepository.findByCourseIdAndId(courseId, id).orElseThrow(() ->
                new ServiceException("Course file selection failed!"));
    }

    @Override
    public List<CourseFile> getByCourseId(Long courseId) {
        try {
            return courseFileRepository.findByCourseId(courseId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Course file selection failed!");
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            courseFileRepository.deleteById(id);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Course file deletion failed!", dataAccessException);
        }
    }

    @Override
    public boolean checkIfExistsByCourseIdAndId(Long courseId, Long id) {
        try {
            return courseFileRepository.existsByCourseIdAndId(courseId, id);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Course file checking failed!", dataAccessException);
        }
    }
}
