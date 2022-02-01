package com.itechart.cargotrucking.core.client.repository;

import com.itechart.cargotrucking.core.client.Client;
import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClientRepository extends QuerydslRepository<Client, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Client SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    boolean existsByName(String name);

    boolean existsByIdAndDeleteDateIsNull(long id);

}
