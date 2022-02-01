package com.itechart.cargotrucking.core.car.dto;

import com.itechart.cargotrucking.core.car.CarType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class CarUpdateDto {
    @NotBlank(message = "Car number cannot be empty")
    @Length(max = 10, message = "Car number length cannot be more than 10 characters")
    private String number;

    @NotNull(message = "Fuel consumption cannot be empty")
    private BigDecimal fuelConsumption;

    @NotNull(message = "Load capacity cannot be empty")
    private Integer loadCapacity;

    @NotNull(message = "Car type cannot be empty")
    private CarType carType;
}
