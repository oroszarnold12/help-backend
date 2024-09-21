package com.help.service.announcement;

import com.help.model.announcement.AnnouncementComment;

public interface AnnouncementCommentService {
    AnnouncementComment save(AnnouncementComment announcementComment);

    AnnouncementComment getByAnnouncementIdAndId(Long announcementId, Long id);

    void deleteById(Long id);
}
