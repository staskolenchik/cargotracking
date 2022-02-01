package com.itechart.cargotrucking.core.email.service;

import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.core.email.EmailMessage;
import com.itechart.cargotrucking.core.email.RestoreEmailMessage;
import com.itechart.cargotrucking.core.email.UserEmailChangeMessage;
import com.itechart.cargotrucking.core.repair.RepairAccountRelations;
import com.itechart.cargotrucking.core.repair.repository.RepairAccountRepository;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.DuplicatedEmailException;
import com.itechart.cargotrucking.core.user.exception.EmailNotFoundException;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.core.userprofile.UserEmailChange;
import com.itechart.cargotrucking.core.userprofile.repository.UserEmailChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String username;

    private JavaMailSender javaMailSender;
    private RepairAccountRepository repairAccountRepository;
    private UserEmailChangeRepository userEmailChangeRepository;
    private UserService userService;
    private ClientService clientService;

    @Autowired
    public EmailServiceImpl(
            JavaMailSender javaMailSender,
            RepairAccountRepository repairAccountRepository,
            UserEmailChangeRepository userEmailChangeRepository,
            UserService userService,
            ClientService clientService
    ) {
        this.javaMailSender = javaMailSender;
        this.repairAccountRepository = repairAccountRepository;
        this.userEmailChangeRepository = userEmailChangeRepository;
        this.userService = userService;
        this.clientService = clientService;
    }

    @Override
    public void sendRestoreLink(RestoreEmailMessage message) {
        validateEmailExist(message.getRecipient());

        User emailUser = userService.findByEmail(message.getRecipient());
        if (emailUser.getDeleteDate() != null || !clientService.existsById(emailUser.getClientId())) {
            throw new EmailNotFoundException("Email %s not found ", message.getRecipient());
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        String uuid = UUID.randomUUID().toString();
        String link = String.format(message.getText(), uuid);
        String subject = StringUtils.isEmpty(message.getSubject()) ? "Restoring password" : message.getSubject();

        mailMessage.setFrom(username);
        mailMessage.setTo(message.getRecipient());
        mailMessage.setSubject(subject);
        mailMessage.setText(link);

        saveRestoreUUID(uuid, emailUser.getId());

        javaMailSender.send(mailMessage);
    }

    @Override
    public void sendSimpleEmail(EmailMessage message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(message.getRecipient());
        mailMessage.setSubject(message.getSubject());
        mailMessage.setText(message.getText());

        javaMailSender.send(mailMessage);
    }

    @Override
    public void sendChangeEmail(long userId, UserEmailChangeMessage message) {
        validateChangeEmail(message.getRecipient());

        User emailUser = userService.findById(userId);
        if (emailUser == null || emailUser.getDeleteDate() != null
                || !clientService.existsById(emailUser.getClientId())
        ) {
            throw new UserNotFoundException("User with id %s not found ", userId);
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        String uuid = UUID.randomUUID().toString();
        String link = String.format(message.getText(), uuid);
        String subject = StringUtils.isEmpty(message.getSubject()) ? "Change email" : message.getSubject();

        mailMessage.setFrom(username);
        mailMessage.setTo(message.getRecipient());
        mailMessage.setSubject(subject);
        mailMessage.setText(link);

        saveEmailUUID(uuid, userId, message.getRecipient());

        javaMailSender.send(mailMessage);
    }
  
    @Override
    public void sendEmailWithRetry(EmailMessage email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getRecipient());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getText());

        javaMailSender.send(mailMessage);
    }
  
    @Override
    public void notifyAdminIfScheduledEmailFailed(MailSendException exception, EmailMessage email) {
        String message = "Couldn't send email:\n" +
                "To: " + email.getRecipient() + "\n" +
                "Subject: " + email.getSubject() + "\n" +
                "Text: " + email.getText() + ".";

        log.warn(message, exception);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("cargo.trucking.test@gmail.com");
        mailMessage.setSubject("Daily birthday celebration email sanding failed");
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    private void validateEmailExist(String email) {
        if (!userService.existsByEmail(email)) {
            throw new EmailNotFoundException("Email %s not found", email);
        }
    }

    private void validateChangeEmail(String email) {
        if (userService.existsByEmail(email)) {
            throw new DuplicatedEmailException("Email %s already used", email);
        }
    }

    private void saveRestoreUUID(String uuid, long userId) {
        RepairAccountRelations relations = new RepairAccountRelations();
        relations.setUid(uuid);
        relations.setUserId(userId);
        relations.setExpDate(LocalDateTime.now().plusMinutes(30));

        repairAccountRepository.save(relations);
    }

    private void saveEmailUUID(String uuid, long userId, String email) {
        UserEmailChange userEmailChange = new UserEmailChange();
        userEmailChange.setUid(uuid);
        userEmailChange.setUserId(userId);
        userEmailChange.setEmail(email);
        userEmailChange.setExpDate(LocalDateTime.now().plusMinutes(30));

        userEmailChangeRepository.save(userEmailChange);
    }
}
