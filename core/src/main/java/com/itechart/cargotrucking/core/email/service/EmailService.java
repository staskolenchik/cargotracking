package com.itechart.cargotrucking.core.email.service;

import com.itechart.cargotrucking.core.email.EmailMessage;
import com.itechart.cargotrucking.core.email.RestoreEmailMessage;
import com.itechart.cargotrucking.core.email.UserEmailChangeMessage;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface EmailService {
    void sendRestoreLink(RestoreEmailMessage message);

    void sendSimpleEmail(EmailMessage message);

    void sendChangeEmail(long userId, UserEmailChangeMessage message);
  
    @Retryable(
            value = {MailSendException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3600000)
    )
    void sendEmailWithRetry(EmailMessage email);

    @Recover
    void notifyAdminIfScheduledEmailFailed(MailSendException exception, EmailMessage email);
}
