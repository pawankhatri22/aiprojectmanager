package com.aiprojectmanager.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduateProfileRepository extends JpaRepository<GraduateProfile, Long> {
    Optional<GraduateProfile> findByUserId(Long userId);
}
