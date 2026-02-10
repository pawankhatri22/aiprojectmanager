package com.aiprojectmanager.zoom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ZoomApiService {
    @Autowired
    private ZoomAuthenticationHelper zoomAuthenticationHelper;

    @Autowired
    RestTemplate restTemplate;

    @Value("${zoom.oauth2.api-url}")
    private String zoomApiUrl;
    private static String BEARER_AUTHORIZATION = "Bearer %s";

    private static final String ZOOM_USER_BASE_URL = "%s/v2/users";


    public ResponseEntity<String> getAllMeetings() {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders authHeader = createBearerAuthHeader(zoomAuthenticationHelper.getAuthenticationToken());
            HttpEntity<String> entity = new HttpEntity<String>(authHeader);
            response = restTemplate.exchange(getUserMeetingListUrl(), HttpMethod.GET, entity, String.class);
            return response;
        } catch (Exception e) {
            //sout is used for demo purposes you could use @Slf4j
            System.out.println(String.format("Unable to get all meetings due to %s. Response code: %d", e.getMessage(), response.getStatusCode()));
            e.printStackTrace();
        }
        return response;
    }

    public String getUserMeetingListUrl() {
        StringBuilder sb = new StringBuilder(
                String.format(ZOOM_USER_BASE_URL, zoomApiUrl));
        sb.append("/me/meetings");
        return sb.toString();
    }

    public static HttpHeaders createBearerAuthHeader(String token) {
        HttpHeaders headers = createHTTPHeader();
        String authToken = String.format(BEARER_AUTHORIZATION, token);
        headers.set(HttpHeaders.AUTHORIZATION, authToken);
        return headers;
    }

    private static HttpHeaders createHTTPHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    public String createMeeting()  {

        try {
            String url = zoomApiUrl + "/v2/users/me/meetings";

            HttpHeaders headers =
                    ZoomApiService.createBearerAuthHeader(
                            zoomAuthenticationHelper.getAuthenticationToken());

            String body = """
                    {
                      "topic":"Mentorship Session",
                      "type":2,
                      "duration":60,
                      "settings":{
                         "join_before_host":true
                      }
                    }
                    """;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // ✅ Parse response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            String joinUrl = root.get("join_url").asText();
            String startUrl = root.get("start_url").asText();

            // You can log or store startUrl if needed
            System.out.println("Host link: " + startUrl);

            return joinUrl;   // <-- return student link
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}