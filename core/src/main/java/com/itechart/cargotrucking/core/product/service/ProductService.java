package com.itechart.cargotrucking.core.product.service;

import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.ProductWriteoff;
import com.itechart.cargotrucking.core.product.dto.ProductAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    @Transactional
    long add(long invoiceId, ProductAddDto productAddDto);

    @Transactional
    void update(long id, ProductUpdateDto productUpdateDto);

    @Transactional
    void delete(long id);

    @Transactional
    void writeoff(long id, short amount, ProductWriteoff writeoff);

    @Transactional
    void writeoffUpdate(long id, short amount, long writeoffedProductId);

    @Transactional
    void writeoffDelete(long id, long writeoffedProductId);

    boolean existsByIdAndAmount(long id, short amount);

    List<ProductInfoDto> findByInvoiceId(long invoiceId);

    Product findByProductWriteoffId(long writeoffId);

    Product findById(long id);

    String getProductNameByWriteoffId(long writeoffId);

    long getClientId(long id);
}
