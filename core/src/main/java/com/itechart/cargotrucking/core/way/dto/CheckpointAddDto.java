package com.itechart.cargotrucking.core.way.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CheckpointAddDto {
    @NotBlank(message = "Checkpoint address must be filled")
    @Length(max = 40, message = "Checkpoint address length cannot be more than 40 characters")
    private String address;

    @NotNull(message = "Checkpoint required arrival date must be filled")
    private LocalDateTime requiredArrivalDate;
}
