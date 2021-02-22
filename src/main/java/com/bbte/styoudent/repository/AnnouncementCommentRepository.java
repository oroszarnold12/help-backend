package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.AnnouncementComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementCommentRepository extends JpaRepository<AnnouncementComment, Long> {
    Optional<AnnouncementComment> findByAnnouncementIdAndId(Long announcementId, Long id);
}
