package com.itechart.cargotrucking.core.product.service;

import com.itechart.cargotrucking.core.product.ProductWriteoff;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductWriteoffService {
    @Transactional
    long add(ProductWriteoffAddDto productWriteoffAddDto);

    @Transactional
    void update(long id, ProductWriteoffUpdateDto productWriteoffUpdateDto);

    @Transactional
    void delete(long... ids);

    List<ProductWriteoffInfoDto> find(long invoiceId);

    ProductWriteoff findById(long id);

    long getClientId(long id);
}
