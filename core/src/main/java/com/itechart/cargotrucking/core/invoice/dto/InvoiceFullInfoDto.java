package com.itechart.cargotrucking.core.invoice.dto;

import com.itechart.cargotrucking.core.product.dto.ProductInfoDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerInfoDto;
import com.itechart.cargotrucking.core.storage.dto.StorageInfoDto;
import com.itechart.cargotrucking.core.user.dto.UserInfoDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceFullInfoDto {
    private long id;
    private String number;
    private LocalDate creationDate;
    private LocalDate verifiedDate;
    private String status;
    private ProductOwnerInfoDto productOwner;
    private StorageInfoDto storage;
    private UserInfoDto driver;
    private List<ProductInfoDto> products;
}
