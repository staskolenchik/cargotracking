package com.itechart.cargotrucking.core.way.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckpointInfoDto {
    private String address;
    private LocalDateTime requiredArrivalDate;
    private LocalDateTime checkpointDate;
}
