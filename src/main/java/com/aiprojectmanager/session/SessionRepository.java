package com.aiprojectmanager.session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Page<Session> findByMentorIdOrGraduateId(Long mentorId, Long graduateId, Pageable pageable);

    List<Session> findByMentorIdAndStatusInAndScheduledTimeBetween(
            Long mentorId,
            List<SessionStatus> statuses,
            LocalDateTime from,
            LocalDateTime to
    );
}
