package com.itechart.cargotrucking.core.car;

import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "delete_date", updatable = false, insertable = false)
    private LocalDateTime deleteDate;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "fuel_consumption", nullable = false)
    private BigDecimal fuelConsumption;

    @Column(name = "load_capacity")
    private int loadCapacity;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private CarType carType;

    @Column(name = "client_company_id", nullable = false)
    private Long clientId;
}
