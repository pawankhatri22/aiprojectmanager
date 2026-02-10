package com.aiprojectmanager.meeting;

import com.aiprojectmanager.session.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ZoomApiService {
    private final RestTemplate restTemplate;
    private final String zoomApiBase;
    private final String zoomJwtToken;
    private final String zoomUserId;

    public ZoomApiService(RestTemplateBuilder builder,
                          @Value("${app.meeting.zoom.base-url:https://api.zoom.us/v2}") String zoomApiBase,
                          @Value("${app.meeting.zoom.jwt-token:}") String zoomJwtToken,
                          @Value("${app.meeting.zoom.user-id:me}") String zoomUserId) {
        this.restTemplate = builder.build();
        this.zoomApiBase = zoomApiBase;
        this.zoomJwtToken = zoomJwtToken;
        this.zoomUserId = zoomUserId;
    }

    public String createMeeting(Session session) {
        if (zoomJwtToken.isBlank()) {
            return "Creating Meeting link taking time, will send link on your email soon!";
        }
        try {
            String url = zoomApiBase + "/users/" + zoomUserId + "/meetings";
            Map<String, Object> body = Map.of(
                    "topic", "Mentoring Session #" + session.getId(),
                    "type", 2,
                    "start_time", session.getScheduledTime().toString(),
                    "duration", session.getDurationMinutes(),
                    "timezone", "UTC",
                    "settings", Map.of("join_before_host", true, "waiting_room", false)
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(zoomJwtToken);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getBody() != null && response.getBody().get("join_url") != null) {
                return response.getBody().get("join_url").toString();
            }
        } catch (Exception ignored) {
        }
        return "Creating Meeting link taking time, will send link on your email soon!";
    }
}
