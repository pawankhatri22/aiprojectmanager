package com.aiprojectmanager;

import com.aiprojectmanager.zoom.ZoomApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AiProjectManagerApplication implements CommandLineRunner {

    @Autowired
    ZoomApiService zoomApiService;


    @Override
    public void run(String... args) throws Exception {
        System.out.println("asdad1 " + zoomApiService.getUserMeetingListUrl());
        //System.out.println("asdad " + zoomApiService.getAllMeetings().getBody());

       // System.out.println("create meeting " + zoomApiService.createMeeting().getBody());
    }

    public static void main(String[] args) {
        SpringApplication.run(AiProjectManagerApplication.class, args);
    }


}
