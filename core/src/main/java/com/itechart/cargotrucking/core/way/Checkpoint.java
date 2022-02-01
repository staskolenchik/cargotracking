package com.itechart.cargotrucking.core.way;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "required_arrival_date", nullable = false)
    private LocalDateTime requiredArrivalDate;

    @Column(name = "checkpoint_date")
    private LocalDateTime checkpointDate;

    @Column(name = "waybill_id", updatable = false)
    private long waybillId;
}
