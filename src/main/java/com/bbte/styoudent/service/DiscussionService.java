package com.bbte.styoudent.service;

import com.bbte.styoudent.model.Discussion;

public interface DiscussionService {
    Discussion getByCourseIdAndId(Long courseId, Long id);

    Discussion save(Discussion discussion);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);

    void delete(Long id);
}
