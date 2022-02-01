package com.itechart.cargotrucking.core.car.dto;

import com.itechart.cargotrucking.core.car.CarType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CarFilterDto {
    private String number;
    private BigDecimal fuelConsumptionLess;
    private BigDecimal fuelConsumptionMore;
    private Integer loadCapacityLess;
    private Integer loadCapacityMore;
    private List<CarType> carTypes;

    public void setCarTypes(CarType[] carTypes) {
        if (carTypes != null) {
            this.carTypes = Arrays.asList(carTypes);
        }
    }
}
