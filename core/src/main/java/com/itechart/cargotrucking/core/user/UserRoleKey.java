package com.itechart.cargotrucking.core.user;

import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserRoleKey implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private UserRoleEnum role;

    public UserRoleKey() {
    }

    public UserRoleKey(Long userId, UserRoleEnum role) {
        this.userId = userId;
        this.role = role;
    }

}
