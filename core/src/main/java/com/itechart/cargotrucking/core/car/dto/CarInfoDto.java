package com.itechart.cargotrucking.core.car.dto;

import com.itechart.cargotrucking.core.car.CarType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CarInfoDto {
    private long id;
    private String number;
    private BigDecimal fuelConsumption;
    private int loadCapacity;
    private CarType carType;
}
