package com.itechart.cargotrucking.core.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class UserPersonDataUpdateDto {
    @Length(max = 20, message = "User name length cannot be more than 20 characters")
    private String name;

    @NotBlank(message = "User surname cannot be empty")
    @Length(max = 20, message = "User surname length cannot be more than 20 characters")
    private String surname;

    @Length(max = 20, message = "User patronymic length cannot be more than 20 characters")
    private String patronymic;

    private LocalDate bornDate;

    @Length(max = 20, message = "User town length cannot be more than 20 characters")
    private String town;

    @Length(max = 20, message = "User street length cannot be more than 20 characters")
    private String street;

    @Length(max = 5, message = "User house length cannot be more than 5 characters")
    private String house;

    @Length(max = 5, message = "User flat length cannot be more than 5 characters")
    private String flat;
}
