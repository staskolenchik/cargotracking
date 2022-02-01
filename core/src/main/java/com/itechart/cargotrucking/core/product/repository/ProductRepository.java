package com.itechart.cargotrucking.core.product.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.product.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends QuerydslRepository<Product, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT p.amount FROM Product p WHERE p.id = ?1 AND p.deleteDate is null")
    short getProductAmountById(long id);

    @Query("SELECT p.invoice.creator.clientId FROM Product p WHERE p.id = ?1 AND p.deleteDate is null")
    long getClientId(long productId);

    @Query("SELECT p.name FROM Product p WHERE p.productWriteoff.id = ?1 AND p.deleteDate is null")
    String getProductNameByWriteoffId(long writeoffId);

    boolean existsByIdAndDeleteDateIsNull(long id);

    @Query("SELECT p FROM Product p WHERE p.invoice.id = ?1 AND p.status <> 'LOST' AND p.deleteDate is null")
    List<Product> findNotLostByInvoiceId(long invoiceId);

    @Query("SELECT p FROM Product p WHERE p.invoice.id = ?1 AND p.deleteDate is null")
    List<Product> findByInvoiceId(long invoiceId);

    Product findByProductWriteoffId(long writeoffId);
}
