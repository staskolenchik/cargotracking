package com.itechart.cargotrucking.core.invoice.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.invoice.Invoice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends QuerydslRepository<Invoice, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Invoice SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT i.creator.clientId FROM Invoice i WHERE i.id = ?1 AND i.deleteDate is null")
    Long getClientId(long id);

    boolean existsByIdAndDeleteDateIsNull(long id);
}
