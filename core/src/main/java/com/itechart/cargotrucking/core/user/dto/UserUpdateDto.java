package com.itechart.cargotrucking.core.user.dto;

import com.itechart.cargotrucking.core.user.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class UserUpdateDto {
    @Length(max = 20, message = "User name length cannot be more than 20 characters")
    private String name;

    @NotBlank(message = "User surname cannot be empty")
    @Length(max = 20, message = "User surname length cannot be more than 20 characters")
    private String surname;

    @Length(max = 20, message = "User patronymic length cannot be more than 20 characters")
    private String patronymic;

    private LocalDate bornDate;

    @NotBlank(message = "User email cannot be empty")
    @Email(message = "User email is not valid")
    @Length(max = 50, message = "User email length cannot be more than 50 characters")
    private String email;

    @Length(max = 20, message = "User town length cannot be more than 20 characters")
    private String town;

    @Length(max = 20, message = "User street length cannot be more than 20 characters")
    private String street;

    @Length(max = 5, message = "User house length cannot be more than 5 characters")
    private String house;

    @Length(max = 5, message = "User flat length cannot be more than 5 characters")
    private String flat;

    @NotBlank(message = "User login cannot be empty")
    @Length(min = 5, max = 15, message = "User login length must be more than 5 and less than 15 characters")
    private String login;

    @NotBlank(message = "User password cannot be empty")
    @Length(min = 5, max = 72, message = "User password length must be more than 5 and less than 72 characters")
    private String password;

    @NotBlank(message = "User password confirmation cannot be empty")
    @Length(min = 5, max = 72, message = "User password confirmation length must be more than 5 and less than 72 characters")
    private String passwordConfirm;

    @Length(max = 30, message = "User passport number length cannot be more than 30 characters")
    private String passportNum;

    @Length(max = 50, message = "User passport issuer length cannot be more than 50 characters")
    private String issuedBy;

    @NotNull(message = "User roles cannot be null")
    private List<UserRoleEnum> userRoles;

    @NotNull(message = "Change password property cannot be null")
    private Boolean isChangePassword;

    public boolean isPasswordsEquals() {
        return password.equals(passwordConfirm);
    }
}
