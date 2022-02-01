package com.itechart.cargotrucking.core.car.repository;

import com.itechart.cargotrucking.core.car.Car;
import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends QuerydslRepository<Car, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Car SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    boolean existsByIdAndDeleteDateIsNull(long id);

    boolean existsByNumber(String number);

    @Query("SELECT CASE WHEN (count(c) > 0) THEN TRUE ELSE FALSE END FROM Car c WHERE c.id = ?1 AND c.deleteDate is null AND c.loadCapacity >= ?2")
    boolean existsByIdAndLoadCapacity(long id, int loadCapacity);

    @Query("SELECT c.clientId FROM Car c WHERE c.id = ?1 AND c.deleteDate is null")
    Long getClientId(long id);
}
