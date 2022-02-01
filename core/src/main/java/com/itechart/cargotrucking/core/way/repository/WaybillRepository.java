package com.itechart.cargotrucking.core.way.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.way.Waybill;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WaybillRepository extends QuerydslRepository<Waybill, Long> {
    boolean existsByInvoiceId(long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Waybill SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT w FROM Waybill w WHERE w.deleteDate is null AND w.status = 'FINISHED_CARRIAGE' AND w.verifier.clientId = ?1")
    List<Waybill> findByClientId(long clientId);

    @Query("SELECT w FROM Waybill w WHERE w.deleteDate is null AND w.status = 'FINISHED_CARRIAGE' AND w.verifier.clientId = ?1 AND w.factualEndDate >= ?2 AND w.factualEndDate <= ?3")
    List<Waybill> findByClientIdAndDate(long clientId, LocalDateTime startDate, LocalDateTime endDate);
}
