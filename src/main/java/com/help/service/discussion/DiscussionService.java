package com.help.service.discussion;

import com.help.model.discussion.Discussion;

public interface DiscussionService {
    Discussion getByCourseIdAndId(Long courseId, Long id);

    Discussion save(Discussion discussion);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);

    void delete(Long id);
}
