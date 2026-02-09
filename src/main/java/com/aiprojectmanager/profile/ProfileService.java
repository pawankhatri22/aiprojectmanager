package com.aiprojectmanager.profile;

import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
    private final MentorProfileRepository mentorRepo;
    private final GraduateProfileRepository graduateRepo;
    private final CurrentUserService currentUserService;

    public ProfileService(MentorProfileRepository mentorRepo, GraduateProfileRepository graduateRepo, CurrentUserService currentUserService) {
        this.mentorRepo = mentorRepo;
        this.graduateRepo = graduateRepo;
        this.currentUserService = currentUserService;
    }

    public ProfileDtos.MentorProfileResponse upsertMentor(ProfileDtos.MentorProfileRequest req) {
        var user = currentUserService.get();
        if (user.getRole() != Role.MENTOR) throw new IllegalArgumentException("Mentor role required");
        MentorProfile profile = mentorRepo.findByUserId(user.getId()).orElse(MentorProfile.builder().user(user).ratingAverage(0.0).build());
        profile.setFullName(req.fullName()); profile.setCompany(req.company()); profile.setJobTitle(req.jobTitle());
        profile.setYearsExperience(req.yearsExperience()); profile.setBio(req.bio()); profile.setHourlyRate(req.hourlyRate()); profile.setPhotoUrl(req.photoUrl());
        profile.getExpertise().clear();
        if (req.expertise() != null) req.expertise().forEach(tag -> profile.getExpertise().add(MentorExpertise.builder().mentorProfile(profile).tag(tag).build()));
        return toMentor(mentorRepo.save(profile));
    }

    public ProfileDtos.GraduateProfileResponse upsertGraduate(ProfileDtos.GraduateProfileRequest req) {
        var user = currentUserService.get();
        if (user.getRole() != Role.GRADUATE) throw new IllegalArgumentException("Graduate role required");
        GraduateProfile profile = graduateRepo.findByUserId(user.getId()).orElse(GraduateProfile.builder().user(user).build());
        profile.setFullName(req.fullName()); profile.setEducation(req.education()); profile.setCareerGoal(req.careerGoal()); profile.setResumeUrl(req.resumeUrl());
        profile.getSkills().clear();
        if (req.skills() != null) req.skills().forEach(skill -> profile.getSkills().add(GraduateSkill.builder().graduateProfile(profile).skill(skill).build()));
        return toGraduate(graduateRepo.save(profile));
    }

    public ProfileDtos.GraduateProfileResponse myGraduateProfile() {
        var user = currentUserService.get();
        return toGraduate(graduateRepo.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("Profile not found")));
    }

    public ProfileDtos.MentorProfileResponse mentorById(Long id) {
        return toMentor(mentorRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mentor not found")));
    }

    public ProfileDtos.MentorProfileResponse toMentor(MentorProfile p) {
        return new ProfileDtos.MentorProfileResponse(p.getId(), p.getUser().getId(), p.getFullName(), p.getCompany(), p.getJobTitle(),
                p.getYearsExperience(), p.getBio(), p.getHourlyRate(), p.getRatingAverage(), p.getPhotoUrl(),
                p.getExpertise().stream().map(MentorExpertise::getTag).toList());
    }

    private ProfileDtos.GraduateProfileResponse toGraduate(GraduateProfile p) {
        return new ProfileDtos.GraduateProfileResponse(p.getId(), p.getUser().getId(), p.getFullName(), p.getEducation(), p.getCareerGoal(),
                p.getResumeUrl(), p.getSkills().stream().map(GraduateSkill::getSkill).toList());
    }
}
