package com.bbte.styoudent.service;

import com.bbte.styoudent.model.CourseFile;

import java.util.List;

public interface CourseFileService {
    CourseFile save(CourseFile courseFile);

    CourseFile getByCourseIdAndId(Long courseId, Long id);

    List<CourseFile> getByCourseId(Long courseId);

    void deleteById(Long id);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);
}
