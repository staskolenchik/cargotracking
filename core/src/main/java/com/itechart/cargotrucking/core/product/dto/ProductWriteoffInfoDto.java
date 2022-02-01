package com.itechart.cargotrucking.core.product.dto;

import com.itechart.cargotrucking.core.product.ProductWriteoffStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWriteoffInfoDto {
    private long id;
    private String name;
    private short amount;
    private ProductWriteoffStatus status;
    private long productId;
}
