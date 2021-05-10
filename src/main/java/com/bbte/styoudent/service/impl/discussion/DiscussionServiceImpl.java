package com.bbte.styoudent.service.impl.discussion;

import com.bbte.styoudent.model.discussion.Discussion;
import com.bbte.styoudent.repository.discussion.DiscussionRepository;
import com.bbte.styoudent.service.discussion.DiscussionService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DiscussionServiceImpl implements DiscussionService {
    private final DiscussionRepository discussionRepository;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    @Override
    public Discussion getByCourseIdAndId(Long courseId, Long id) {

        return discussionRepository.findByCourseIdAndId(courseId, id).orElseThrow(() ->
                new ServiceException("Discussion selection with id: " + id + " failed!")
        );
    }

    @Override
    public Discussion save(Discussion discussion) {
        try {
            return discussionRepository.save(discussion);
        } catch (DataAccessException de) {
            throw new ServiceException("Discussion insertion failed!", de);
        }
    }

    @Override
    public boolean checkIfExistsByCourseIdAndId(Long courseId, Long id) {
        try {
            return discussionRepository.existsByCourseIdAndId(courseId, id);
        } catch (DataAccessException de) {
            throw new ServiceException("Discussion checking failed!", de);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            discussionRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Discussion deletion failed!", de);
        }
    }
}
