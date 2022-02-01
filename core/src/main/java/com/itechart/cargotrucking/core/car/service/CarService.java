package com.itechart.cargotrucking.core.car.service;

import com.itechart.cargotrucking.core.car.dto.CarAddDto;
import com.itechart.cargotrucking.core.car.dto.CarFilterDto;
import com.itechart.cargotrucking.core.car.dto.CarInfoDto;
import com.itechart.cargotrucking.core.car.dto.CarUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface CarService {
    @Transactional
    long add(CarAddDto carAddDto);

    @Transactional
    void update(long id, CarUpdateDto carUpdateDto);

    @Transactional
    void delete(long... ids);

    Page<CarInfoDto> find(long clientId, CarFilterDto carFilterDto, Pageable pageable);

    CarInfoDto findById(long id);

    boolean existsById(long id);

    Long getClientId(long id);
}
