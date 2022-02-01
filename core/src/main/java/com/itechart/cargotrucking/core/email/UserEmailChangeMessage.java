package com.itechart.cargotrucking.core.email;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserEmailChangeMessage {
    @NotBlank(message = "Message recipient cannot be empty")
    private String recipient;

    private String subject;

    @NotBlank(message = "Message text cannot be empty")
    private String text;

    @NotBlank(message = "User password cannot be empty")
    @Length(min = 5, max = 72, message = "User password length must be more than 5 and less than 72 characters")
    private String password;
}
