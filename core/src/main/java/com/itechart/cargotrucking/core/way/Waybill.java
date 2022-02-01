package com.itechart.cargotrucking.core.way;

import com.itechart.cargotrucking.core.car.Car;
import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import com.itechart.cargotrucking.core.invoice.Invoice;
import com.itechart.cargotrucking.core.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
public class Waybill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "distance", nullable = false, updatable = false)
    private int distance;

    @Column(name = "start_point", nullable = false)
    private String startPoint;

    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "factual_start_date")
    private LocalDateTime factualStartDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "factual_end_date")
    private LocalDateTime factualEndDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private CarriageStatus status;

    @Column(name = "delete_date", updatable = false, insertable = false)
    private LocalDateTime deleteDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User verifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @OneToMany(fetch =  FetchType.LAZY, mappedBy = "waybillId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Checkpoint> checkpoints;
}
