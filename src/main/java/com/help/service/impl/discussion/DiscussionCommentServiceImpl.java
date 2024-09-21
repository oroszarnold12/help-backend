package com.help.service.impl.discussion;

import com.help.model.discussion.DiscussionComment;
import com.help.repository.discussion.DiscussionCommentRepository;
import com.help.service.discussion.DiscussionCommentService;
import com.help.service.ServiceException;
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
