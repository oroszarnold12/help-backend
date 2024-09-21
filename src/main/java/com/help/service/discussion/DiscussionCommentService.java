package com.help.service.discussion;

import com.help.model.discussion.DiscussionComment;

public interface DiscussionCommentService {
    DiscussionComment save(DiscussionComment discussionComment);

    DiscussionComment getByDiscussionIdAndId(Long discussionId, Long id);

    void delete(Long id);
}
