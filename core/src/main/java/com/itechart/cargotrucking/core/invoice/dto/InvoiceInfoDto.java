package com.itechart.cargotrucking.core.invoice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InvoiceInfoDto {
    private long id;
    private String number;
    private LocalDate creationDate;
    private LocalDate verifiedDate;
    private String status;
    private long productOwnerId;
    private long storageId;
    private long driverId;
}
