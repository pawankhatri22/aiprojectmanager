package com.aiprojectmanager.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUserId(Long userId);

    @Query("""
            select distinct m from MentorProfile m
            left join m.expertise e
            where lower(m.fullName) like lower(concat('%', :term, '%'))
               or lower(coalesce(e.tag, '')) like lower(concat('%', :term, '%'))
            """)
    Page<MentorProfile> searchByNameOrExpertise(String term, Pageable pageable);
}
