package com.itechart.cargotrucking.webapp.security.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "Login cannot be empty")
    @Length(min = 5, max = 15, message = "User login length must be more than 5 and less than 15 characters")
    private String login;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 5, max = 25, message = "User password length must be more than 5 and less than 25 characters")
    private String password;

    @NotBlank(message = "IP cannot be empty")
    @Pattern(regexp = "([0-9]{1,3}[.]){3}[0-9]{1,3}", message = "IP does not match regexp")
    private String ip;
}
