package com.itechart.cargotrucking.core.product;

import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import com.itechart.cargotrucking.core.invoice.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "delete_date", insertable = false)
    private LocalDateTime deleteDate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private short amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_writeoff_id")
    private ProductWriteoff productWriteoff;
}
