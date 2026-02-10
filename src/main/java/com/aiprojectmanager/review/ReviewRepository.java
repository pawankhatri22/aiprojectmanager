package com.aiprojectmanager.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findBySessionId(Long sessionId);
    List<Review> findByMentorIdOrderByCreatedAtDesc(Long mentorId);
    List<Review> findByMentorId(Long mentorId);
}
