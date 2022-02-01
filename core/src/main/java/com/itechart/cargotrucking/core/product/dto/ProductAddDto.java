package com.itechart.cargotrucking.core.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductAddDto {
    @NotBlank(message = "Product name cannot be empty")
    @Length(max = 50, message = "Product name length cannot be more than 50 characters")
    private String name;

    @NotNull(message = "Product amount cannot be empty")
    private Short amount;
}
