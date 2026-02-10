package com.aiprojectmanager.meeting;

import com.aiprojectmanager.session.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Service
public class MeetingLinkService {
    private final RestTemplate restTemplate;
    private final String googleApiKey;
    private final String googleCalendarId;
    private final String googleOAuthToken;

    public MeetingLinkService(RestTemplateBuilder restTemplateBuilder,
                              @Value("${app.meeting.google.api-key:}") String googleApiKey,
                              @Value("${app.meeting.google.calendar-id:primary}") String googleCalendarId,
                              @Value("${app.meeting.google.oauth-token:}") String googleOAuthToken) {
        this.restTemplate = restTemplateBuilder.build();
        this.googleApiKey = googleApiKey;
        this.googleCalendarId = googleCalendarId;
        this.googleOAuthToken = googleOAuthToken;
    }

    public String createMeetingLink(Session session) {
        if (googleApiKey.isBlank() || googleOAuthToken.isBlank()) {
            return fallbackLink();
        }

        try {
            String url = "https://www.googleapis.com/calendar/v3/calendars/" + googleCalendarId +
                    "/events?conferenceDataVersion=1&key=" + googleApiKey;

            Map<String, Object> body = Map.of(
                    "summary", "Mentoring Session #" + session.getId(),
                    "description", "Mentor: " + session.getMentor().getEmail() + ", Graduate: " + session.getGraduate().getEmail(),
                    "start", Map.of("dateTime", session.getScheduledTime().atOffset(ZoneOffset.UTC).toString(), "timeZone", "UTC"),
                    "end", Map.of("dateTime", session.getScheduledTime().plusMinutes(session.getDurationMinutes()).atOffset(ZoneOffset.UTC).toString(), "timeZone", "UTC"),
                    "conferenceData", Map.of(
                            "createRequest", Map.of(
                                    "requestId", UUID.randomUUID().toString(),
                                    "conferenceSolutionKey", Map.of("type", "hangoutsMeet")
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(googleOAuthToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            Map conferenceData = (Map) response.getBody().get("conferenceData");
            if (conferenceData != null) {
                Object entryPointsObj = conferenceData.get("entryPoints");
                if (entryPointsObj instanceof java.util.List<?> entryPoints && !entryPoints.isEmpty()) {
                    Object first = entryPoints.get(0);
                    if (first instanceof Map<?, ?> m && m.get("uri") != null) {
                        return m.get("uri").toString();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return fallbackLink();
    }

    private String fallbackLink() {
        return "https://meet.aiprojectmanager.local/room/" + UUID.randomUUID();
    }
}
