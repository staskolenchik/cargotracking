package com.itechart.cargotrucking.core.client.service;

import com.itechart.cargotrucking.core.client.Client;

import javax.transaction.Transactional;

public interface ClientStatusService {
    @Transactional
    void activateClient(Client client);

    @Transactional
    void inactivateClient(long id);
}
