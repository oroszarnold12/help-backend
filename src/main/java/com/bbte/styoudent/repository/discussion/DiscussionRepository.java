package com.bbte.styoudent.repository.discussion;

import com.bbte.styoudent.model.discussion.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Optional<Discussion> findByCourseIdAndId(Long courseId, Long id);

    boolean existsByCourseIdAndId(Long courseId, Long id);
}
