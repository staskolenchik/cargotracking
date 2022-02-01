package com.itechart.cargotrucking.core.productowner.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.productowner.ProductOwner;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductOwnerRepository extends QuerydslRepository<ProductOwner,Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductOwner SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Query("SELECT p.clientId FROM ProductOwner p WHERE p.id = ?1 AND p.deleteDate is null")
    Long getClientId(long id);

    boolean existsByIdAndDeleteDateIsNull(long id);
}
