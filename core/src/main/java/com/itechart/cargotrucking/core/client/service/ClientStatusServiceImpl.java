package com.itechart.cargotrucking.core.client.service;

import com.itechart.cargotrucking.core.client.Client;
import com.itechart.cargotrucking.core.client.ClientStatusEnum;
import com.itechart.cargotrucking.core.client.ClientStatusHistory;
import com.itechart.cargotrucking.core.client.repository.ClientStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Validated
public class ClientStatusServiceImpl implements ClientStatusService {

    private ClientStatusRepository clientStatusRepository;

    @Autowired
    public ClientStatusServiceImpl(ClientStatusRepository clientStatusRepository){
        this.clientStatusRepository = clientStatusRepository;
    }

    @Override
    public void activateClient(Client client) {
        ClientStatusHistory clientStatusHistory = new ClientStatusHistory();
        clientStatusHistory.setClientId(client.getId());
        clientStatusHistory.setStatus(ClientStatusEnum.ACTIVATED);
        clientStatusHistory.setDate(LocalDateTime.now());
        clientStatusRepository.save(clientStatusHistory);
    }

    @Override
    public void inactivateClient(long id) {
        ClientStatusHistory clientStatusHistory = new ClientStatusHistory();
        clientStatusHistory.setClientId(id);
        clientStatusHistory.setStatus(ClientStatusEnum.INACTIVED);
        clientStatusHistory.setDate(LocalDateTime.now());
        clientStatusRepository.save(clientStatusHistory);
    }
}
