package com.help.repository.discussion;

import com.help.model.discussion.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    Optional<DiscussionComment> findByDiscussionIdAndId(Long discussionId, Long id);
}
