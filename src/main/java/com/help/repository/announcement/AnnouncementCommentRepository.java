package com.help.repository.announcement;

import com.help.model.announcement.AnnouncementComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementCommentRepository extends JpaRepository<AnnouncementComment, Long> {
    Optional<AnnouncementComment> findByAnnouncementIdAndId(Long announcementId, Long id);
}
