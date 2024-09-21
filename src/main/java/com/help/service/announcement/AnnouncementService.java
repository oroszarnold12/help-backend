package com.help.service.announcement;

import com.help.model.announcement.Announcement;

public interface AnnouncementService {
    Announcement getByCourseIdAndId(Long courseId, Long id);

    Announcement save(Announcement announcement);

    void delete(Long id);

    boolean checkIfExistsByCourseIdAndId(Long courseId, Long id);
}
