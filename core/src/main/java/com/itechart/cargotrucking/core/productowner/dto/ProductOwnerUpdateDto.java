package com.itechart.cargotrucking.core.productowner.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ProductOwnerUpdateDto {
    @NotBlank(message = "Product owner name cannot be empty")
    @Length(max = 30, message = "Product owner name cannot have more than 30 symbols")
    private String name;

    @NotBlank(message = "Product owner address cannot be empty")
    @Length(max = 40, message = "Product owner address length cannot be more than 40 characters")
    private String address;
}
