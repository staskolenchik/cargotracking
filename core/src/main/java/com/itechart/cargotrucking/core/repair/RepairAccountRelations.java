package com.itechart.cargotrucking.core.repair;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "repair_account")
public class RepairAccountRelations {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "exp_date", nullable = false)
    private LocalDateTime expDate;
}
