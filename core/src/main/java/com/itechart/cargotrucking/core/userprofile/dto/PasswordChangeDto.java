package com.itechart.cargotrucking.core.userprofile.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordChangeDto {
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "New password cannot be empty")
    @Length(min = 5, max = 72, message = "User password length must be more than 5 and less than 72 characters")
    private String newPassword;
}
