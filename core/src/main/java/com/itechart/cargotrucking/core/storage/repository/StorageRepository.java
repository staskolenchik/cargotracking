package com.itechart.cargotrucking.core.storage.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.storage.Storage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StorageRepository extends QuerydslRepository<Storage, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Storage SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT s.clientId FROM Storage s WHERE s.id = ?1 AND s.deleteDate is null")
    Long getClientId(long id);

    boolean existsByIdAndDeleteDateIsNull(long id);
}
