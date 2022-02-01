package com.itechart.cargotrucking.core.userprofile;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class UserEmailChange {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "exp_date", nullable = false)
    private LocalDateTime expDate;

    @Column(name = "new_email", nullable = false)
    private String email;
}
