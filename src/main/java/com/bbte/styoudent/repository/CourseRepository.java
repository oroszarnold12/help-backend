package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
