package com.itechart.cargotrucking.core.client.service;

import com.itechart.cargotrucking.core.client.dto.ClientAddDto;
import com.itechart.cargotrucking.core.client.dto.ClientFilterDto;
import com.itechart.cargotrucking.core.client.dto.ClientInfoDto;
import com.itechart.cargotrucking.core.client.dto.ClientUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ClientService {
    @Transactional
    long add(ClientAddDto client);

    @Transactional
    void activate(long id);

    @Transactional
    void inactivate(long... id);

    @Transactional
    void update(long id, ClientUpdateDto client);

    Page<ClientInfoDto> find(ClientFilterDto filter, Pageable pageable);

    boolean existsById(long id);

    ClientInfoDto findById(long id);
}
