package com.itechart.cargotrucking.core.product.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.product.ProductWriteoff;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductWriteoffRepository extends QuerydslRepository<ProductWriteoff, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductWriteoff SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT pw.productId FROM ProductWriteoff pw WHERE pw.id = ?1 AND pw.deleteDate is null")
    long getProductId(long writeoffId);

    @Query("SELECT pw.creator.clientId FROM ProductWriteoff pw WHERE pw.id = ?1 AND pw.deleteDate is null")
    long getClientId(long writeoffId);

    boolean existsByIdAndDeleteDateIsNull(long id);
}
