package com.itechart.cargotrucking.core.scheduler;

import com.itechart.cargotrucking.core.email.EmailMessage;
import com.itechart.cargotrucking.core.email.service.EmailService;
import com.itechart.cargotrucking.core.scheduler.exception.GetResourceFileException;
import com.itechart.cargotrucking.core.scheduler.service.TemplateService;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class Scheduler {

    private EmailService emailService;
    private TemplateService templateService;
    private UserRepository userRepository;

    @Autowired
    public Scheduler(EmailService emailService, TemplateService templateService,
                     UserRepository userRepository) {
        this.emailService = emailService;
        this.templateService = templateService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void autoSendEmail() {

        List<User> userList = userRepository.findAllByBornDateEqualsAndDeleteDateIsNull(LocalDate.now());

        for (User user : userList) {
            EmailMessage emailMessage = getEmailMessage(user);
            emailService.sendEmailWithRetry(emailMessage);
        }
    }

    private EmailMessage getEmailMessage(User user) {

        String birthdayCelebration;
        try {
            birthdayCelebration = templateService.getMessage(user);
        } catch (GetResourceFileException e) {
            birthdayCelebration = "Wish you happy birthday";
        }

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipient(user.getEmail());
        emailMessage.setSubject("Happy birthday!");
        emailMessage.setText(birthdayCelebration);

        return emailMessage;
    }
}

