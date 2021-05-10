package com.bbte.styoudent.service.course;

import com.bbte.styoudent.model.course.CourseFile;

import java.util.List;

public interface CourseFileService {
    CourseFile save(CourseFile courseFile);

    CourseFile getByCourseIdAndId(Long courseId, Long id);

    List<CourseFile> getByCourseId(Long courseId);

    void deleteById(Long id);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);
}
