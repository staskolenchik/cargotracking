package com.itechart.cargotrucking.webapp.template;

import com.itechart.cargotrucking.core.scheduler.service.TemplateService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/template")
public class TemplateController {
    private TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public void saveTemplate(@RequestBody String template, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        Long clientId = userCredentials.getClientId();
        templateService.saveTemplate(template, clientId);
    }
}

