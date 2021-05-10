package com.bbte.styoudent.service.announcement;

import com.bbte.styoudent.model.announcement.AnnouncementComment;

public interface AnnouncementCommentService {
    AnnouncementComment save(AnnouncementComment announcementComment);

    AnnouncementComment getByAnnouncementIdAndId(Long announcementId, Long id);

    void deleteById(Long id);
}
