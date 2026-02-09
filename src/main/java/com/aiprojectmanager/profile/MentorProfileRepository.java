package com.aiprojectmanager.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUserId(Long userId);
    Page<MentorProfile> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
