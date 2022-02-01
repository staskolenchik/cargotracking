package com.itechart.cargotrucking.core.productowner;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ProductOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "delete_date", insertable = false, updatable = false)
    private LocalDate deleteDate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "client_id", nullable = false)
    private Long clientId;
}
