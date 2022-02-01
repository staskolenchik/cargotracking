package com.itechart.cargotrucking.core.user;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class UserRole {
    @EmbeddedId
    private UserRoleKey id;

    @Column(name = "cancel_date")
    private LocalDate cancelDate;

    public UserRole() {
    }

    public UserRole(UserRoleKey id) {
        this.id = id;
    }

}
