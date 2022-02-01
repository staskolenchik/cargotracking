package com.itechart.cargotrucking.core.client;

import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ClientStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private ClientStatusEnum status;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "client_id")
    private Long clientId;
}
