package com.bbte.styoudent.service;

import com.bbte.styoudent.model.DiscussionComment;

public interface DiscussionCommentService {
    DiscussionComment save(DiscussionComment discussionComment);

    DiscussionComment getByDiscussionIdAndId(Long discussionId, Long id);

    void delete(Long id);
}
