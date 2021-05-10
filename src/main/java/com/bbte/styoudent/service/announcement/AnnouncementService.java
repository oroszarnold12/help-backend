package com.bbte.styoudent.service.announcement;

import com.bbte.styoudent.model.announcement.Announcement;

public interface AnnouncementService {
    Announcement getByCourseIdAndId(Long courseId, Long id);

    Announcement save(Announcement announcement);

    void delete(Long id);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);
}
