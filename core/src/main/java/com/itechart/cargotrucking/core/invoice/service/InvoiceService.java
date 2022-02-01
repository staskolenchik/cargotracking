package com.itechart.cargotrucking.core.invoice.service;

import com.itechart.cargotrucking.core.invoice.dto.InvoiceAddDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFilterDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFullInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface InvoiceService {
    @Transactional
    long add(InvoiceAddDto invoice);

    @Transactional
    void update(long id, InvoiceUpdateDto invoice);

    @Transactional
    void delete(long... ids);

    boolean existsById(long id);

    Page<InvoiceInfoDto> find(long clientId, InvoiceFilterDto filter, Pageable pageable);

    Long getClientId(long id);

    InvoiceFullInfoDto findInfoById(long id);
}
