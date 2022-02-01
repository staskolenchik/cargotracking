package com.itechart.cargotrucking.core.repair.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RestorePasswordDto {
    @NotBlank(message = "Password can't be empty")
    @Length(max = 20, message = "Password can't be more than 50 symbols")
    private String password;

    @NotBlank(message = "Password can't be empty")
    @Length(max = 20, message = "Password can't be more than 50 symbols")
    private String passwordConfirm;

    public boolean comparePasswords() {
        return password.equals(passwordConfirm);
    }
}
