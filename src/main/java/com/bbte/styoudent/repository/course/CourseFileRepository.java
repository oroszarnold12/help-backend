package com.bbte.styoudent.repository.course;

import com.bbte.styoudent.model.course.CourseFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseFileRepository extends JpaRepository<CourseFile, Long> {
    List<CourseFile> findByCourseId(Long courseId);

    Optional<CourseFile> findByCourseIdAndId(Long courseId, Long id);

    boolean existsByCourseIdAndId(Long courseId, Long id);
}
