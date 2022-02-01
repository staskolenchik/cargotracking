package com.itechart.cargotrucking.core.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClientInfoDto {
    private long id;
    private String name;
    private String status;
    private LocalDateTime deleteDate;
}
