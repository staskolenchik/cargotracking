package com.itechart.cargotrucking.core.client.repository;

import com.itechart.cargotrucking.core.client.ClientStatusHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientStatusRepository extends JpaRepository<ClientStatusHistory,Long> {
    @Query("SELECT c FROM ClientStatusHistory c WHERE c.date BETWEEN ?1 AND ?2")
    List<ClientStatusHistory> findAll(LocalDateTime start, LocalDateTime end, Sort sort);
}
