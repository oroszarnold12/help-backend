package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.AnnouncementComment;
import com.bbte.styoudent.repository.AnnouncementCommentRepository;
import com.bbte.styoudent.service.AnnouncementCommentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementCommentServiceImpl implements AnnouncementCommentService {
    private final AnnouncementCommentRepository announcementCommentRepository;

    public AnnouncementCommentServiceImpl(AnnouncementCommentRepository announcementCommentRepository) {
        this.announcementCommentRepository = announcementCommentRepository;
    }

    @Override
    public AnnouncementComment save(AnnouncementComment announcementComment) {
        try {
            return announcementCommentRepository.save(announcementComment);
        } catch (DataAccessException de) {
            throw new ServiceException("Announcement comment insertion failed!", de);
        }
    }

    @Override
    public AnnouncementComment getByAnnouncementIdAndId(Long announcementId, Long id) {
        return announcementCommentRepository.findByAnnouncementIdAndId(announcementId, id).orElseThrow(() ->
                new ServiceException("Announcement comment selection with id: " + id + " failed!"));
    }

    @Override
    public void deleteById(Long id) {
        try {
            announcementCommentRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Announcement comment deletion with id: " + id + " failed!");
        }
    }
}
