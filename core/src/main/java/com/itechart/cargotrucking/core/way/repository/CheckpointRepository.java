package com.itechart.cargotrucking.core.way.repository;

import com.itechart.cargotrucking.core.way.Checkpoint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    List<Checkpoint> findByWaybillId(long waybillId);

    @Query("SELECT c FROM Checkpoint c WHERE c.waybillId = ?1 AND c.checkpointDate is null")
    List<Checkpoint> getNextWaybillCheckpoint(long waybillId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Checkpoint SET checkpointDate = now() WHERE id = ?1")
    void reachCheckpoint(long id);
}
