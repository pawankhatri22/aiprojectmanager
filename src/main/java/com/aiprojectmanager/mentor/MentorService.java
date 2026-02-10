package com.aiprojectmanager.mentor;

import com.aiprojectmanager.profile.MentorProfileRepository;
import com.aiprojectmanager.profile.ProfileDtos;
import com.aiprojectmanager.profile.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MentorService {
    private final MentorProfileRepository mentorProfileRepository;
    private final ProfileService profileService;

    public MentorService(MentorProfileRepository mentorProfileRepository, ProfileService profileService) {
        this.mentorProfileRepository = mentorProfileRepository;
        this.profileService = profileService;
    }

    public Page<ProfileDtos.MentorProfileResponse> list(String search, Pageable pageable) {
        String term = search == null ? "" : search.trim();
        return mentorProfileRepository.searchByNameOrExpertise(term, pageable).map(profileService::toMentor);
    }
}
