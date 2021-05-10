package com.bbte.styoudent.service.discussion;

import com.bbte.styoudent.model.discussion.DiscussionComment;

public interface DiscussionCommentService {
    DiscussionComment save(DiscussionComment discussionComment);

    DiscussionComment getByDiscussionIdAndId(Long discussionId, Long id);

    void delete(Long id);
}
