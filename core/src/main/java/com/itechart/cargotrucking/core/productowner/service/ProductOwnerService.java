package com.itechart.cargotrucking.core.productowner.service;

import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerAddDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerFilterDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerInfoDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ProductOwnerService {
    @Transactional
    long add(ProductOwnerAddDto productOwner);

    @Transactional
    void update(long id, ProductOwnerUpdateDto productOwner);

    @Transactional
    void delete(long... ids);

    Page<ProductOwnerInfoDto> find(long clientId, ProductOwnerFilterDto productOwner, Pageable pageable);

    ProductOwnerInfoDto findById(long id);

    boolean existsById(long id);

    Long getClientId(long id);
}
