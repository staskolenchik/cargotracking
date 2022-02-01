package com.itechart.cargotrucking.core.repair.repository;

import com.itechart.cargotrucking.core.repair.RepairAccountRelations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RepairAccountRepository extends JpaRepository<RepairAccountRelations, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RepairAccountRelations SET uid = '' WHERE user_id = ?1")
    void delete(long userId);

    @Query("SELECT CASE WHEN (count(r) > 0) THEN TRUE ELSE FALSE END FROM RepairAccountRelations r WHERE r.uid = ?1 AND r.expDate > now()")
    boolean existsByUid(String uuid);

    RepairAccountRelations findByUid(String uuid);
}
