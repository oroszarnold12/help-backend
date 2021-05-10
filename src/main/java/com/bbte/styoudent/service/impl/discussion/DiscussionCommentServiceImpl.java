package com.bbte.styoudent.service.impl.discussion;

import com.bbte.styoudent.model.discussion.DiscussionComment;
import com.bbte.styoudent.repository.discussion.DiscussionCommentRepository;
import com.bbte.styoudent.service.discussion.DiscussionCommentService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DiscussionCommentServiceImpl implements DiscussionCommentService {
    private final DiscussionCommentRepository discussionCommentRepository;

    public DiscussionCommentServiceImpl(DiscussionCommentRepository discussionCommentRepository) {
        this.discussionCommentRepository = discussionCommentRepository;
    }

    @Override
    public DiscussionComment save(DiscussionComment discussionComment) {
        try {
            return discussionCommentRepository.save(discussionComment);
        } catch (DataAccessException de) {
            throw new ServiceException("Discussion comment insertion failed!", de);
        }
    }

    @Override
    public DiscussionComment getByDiscussionIdAndId(Long discussionId, Long id) {
        return discussionCommentRepository.findByDiscussionIdAndId(discussionId, id).orElseThrow(() ->
                new ServiceException("Discussion selection with id: " + id + " failed!")
        );
    }

    @Override
    public void delete(Long id) {
        try {
            discussionCommentRepository.deleteById(id);
        } catch (DataAccessException de) {
            throw new ServiceException("Discussion comment deletion failed!", de);
        }
    }
}
