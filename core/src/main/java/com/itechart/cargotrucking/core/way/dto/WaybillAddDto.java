package com.itechart.cargotrucking.core.way.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WaybillAddDto {
    @NotNull(message = "Invoice id must be filled")
    private Long invoiceId;

    @NotNull(message = "Distance cannot be null")
    private Integer distance;

    @NotNull(message = "Car id must be filled")
    private Long carId;

    @Future(message = "Date must be greater than today")
    private LocalDateTime endDate;

    private Long verifierId;

    private List<CheckpointAddDto> checkpoints;
}
