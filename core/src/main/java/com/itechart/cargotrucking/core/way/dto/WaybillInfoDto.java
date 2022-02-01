package com.itechart.cargotrucking.core.way.dto;

import com.itechart.cargotrucking.core.way.CarriageStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class WaybillInfoDto {
    private long id;
    private String sender;
    private String receiver;
    private String invoiceNumber;
    private String carNumber;
    private LocalDateTime startDate;
    private CarriageStatus status;
    private long invoiceId;
    private long carId;
}