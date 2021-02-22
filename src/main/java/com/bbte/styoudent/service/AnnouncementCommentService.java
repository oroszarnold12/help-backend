package com.bbte.styoudent.service;

import com.bbte.styoudent.model.AnnouncementComment;

public interface AnnouncementCommentService {
    AnnouncementComment save(AnnouncementComment announcementComment);

    AnnouncementComment getByAnnouncementIdAndId(Long announcementId, Long id);

    void deleteById(Long id);
}
