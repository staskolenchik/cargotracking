package com.itechart.cargotrucking.webapp.email;

import com.itechart.cargotrucking.core.email.EmailMessage;
import com.itechart.cargotrucking.core.email.RestoreEmailMessage;
import com.itechart.cargotrucking.core.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public void sendEmail(@RequestBody @Valid EmailMessage emailMessage) {
      emailService.sendSimpleEmail(emailMessage);
    }

    @PostMapping("/repairing")
    public void repairAccount(@RequestBody @Valid RestoreEmailMessage emailMessage) {
        emailService.sendRestoreLink(emailMessage);
    }
}
