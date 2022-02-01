package com.itechart.cargotrucking.webapp.about;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/api/about")
    public ApplicationInfo getInfo() {
        ApplicationInfo response = new ApplicationInfo();
        response.setApplicationName(name);
        return response;
    }
}
