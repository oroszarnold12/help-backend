package com.help.repository.announcement;

import com.help.model.announcement.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<Announcement> findByCourseIdAndId(Long courseId, Long id);

    boolean existsByCourseIdAndId(Long courseId, Long id);
}
