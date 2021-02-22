package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    Optional<DiscussionComment> findByDiscussionIdAndId(Long discussionId, Long id);
}
