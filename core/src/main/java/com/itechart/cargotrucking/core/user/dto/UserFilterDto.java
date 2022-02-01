package com.itechart.cargotrucking.core.user.dto;

import com.itechart.cargotrucking.core.user.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserFilterDto {
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate beforeBornDate;
    private LocalDate afterBornDate;
    private String town;
    private String street;
    private String house;
    private String flat;
    private Set<UserRoleEnum> userRoles;

    public void setUserRoles(UserRoleEnum[] userRoles) {
        if (userRoles != null) {
            this.userRoles = new HashSet<>(Arrays.asList(userRoles));
        }
    }
}
