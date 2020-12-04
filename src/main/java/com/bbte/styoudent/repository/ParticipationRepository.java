package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    void deleteParticipationsByCourse(Course course);
}
