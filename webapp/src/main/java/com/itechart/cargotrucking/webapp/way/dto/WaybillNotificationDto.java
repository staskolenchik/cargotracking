package com.itechart.cargotrucking.webapp.way.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WaybillNotificationDto {
    private LocalDateTime achieveTime;
    private String checkpointAddress;
}
