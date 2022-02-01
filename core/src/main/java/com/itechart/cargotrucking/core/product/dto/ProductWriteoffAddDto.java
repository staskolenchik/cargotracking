package com.itechart.cargotrucking.core.product.dto;

import com.itechart.cargotrucking.core.product.ProductWriteoffStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductWriteoffAddDto {
    @NotNull(message = "Product id cannot be null")
    private Long productId;

    @NotNull(message = "Product amount cannot be null")
    private Short amount;

    @NotNull(message = "Product status cannot be null")
    private ProductWriteoffStatus status;

    private long creatorId;
}
