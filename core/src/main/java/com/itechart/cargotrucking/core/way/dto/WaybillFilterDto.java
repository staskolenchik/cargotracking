package com.itechart.cargotrucking.core.way.dto;

import com.itechart.cargotrucking.core.way.CarriageStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class WaybillFilterDto {
    Set<CarriageStatus> carriageStatuses;

    public void setCarriageStatuses(CarriageStatus[] carriageStatuses) {
        if (carriageStatuses != null) {
            this.carriageStatuses = new HashSet<>(Arrays.asList(carriageStatuses));
        }
    }
}
