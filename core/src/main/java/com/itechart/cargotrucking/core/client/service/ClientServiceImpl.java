package com.itechart.cargotrucking.core.client.service;

import com.itechart.cargotrucking.core.client.Client;
import com.itechart.cargotrucking.core.client.QClient;
import com.itechart.cargotrucking.core.client.dto.ClientAddDto;
import com.itechart.cargotrucking.core.client.dto.ClientFilterDto;
import com.itechart.cargotrucking.core.client.dto.ClientInfoDto;
import com.itechart.cargotrucking.core.client.dto.ClientUpdateDto;
import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.exception.DuplicatedClientNameException;
import com.itechart.cargotrucking.core.client.exception.InvalidDateException;
import com.itechart.cargotrucking.core.client.repository.ClientRepository;
import com.itechart.cargotrucking.core.email.service.EmailService;
import com.itechart.cargotrucking.core.user.UserRoleEnum;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@Validated
public class ClientServiceImpl implements ClientService {
    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    private ClientRepository clientRepository;
    private ModelMapper modelMapper;
    private ClientStatusService clientStatusService;
    private UserService userService;

    @Autowired
    public ClientServiceImpl(
            ClientRepository clientRepository,
            ModelMapper modelMapper,
            UserService userService,
            ClientStatusService clientStatusService
    ) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.clientStatusService = clientStatusService;
    }

    @Override
    public long add(ClientAddDto clientAddDto) {
        addingValidation(clientAddDto);
        Set<UserRoleEnum> roles = new HashSet<>();
        roles.add(UserRoleEnum.ADMIN);

        clientAddDto.getAdminInfo().setUserRoles(roles);
        Client client = modelMapper.map(clientAddDto, Client.class);

        clientRepository.save(client);

        clientAddDto.getAdminInfo().setClientId(client.getId());
        userService.add(clientAddDto.getAdminInfo());

        clientStatusService.activateClient(client);

        return client.getId();
    }

    @Override
    public void activate(long id) {
        validateActivate(id);

        Client client = clientRepository.getOne(id);
        client.setDeleteDate(null);
        clientStatusService.activateClient(client);
    }

    @Override
    public void inactivate(long... ids) {
        for (long id : ids) {
            deleteValidation(id);
            clientRepository.delete(id);
            clientStatusService.inactivateClient(id);
        }
    }

    @Override
    public void update(long id, ClientUpdateDto clientUpdateDto) {
        updateValidation(id);
        Client client = clientRepository.getOne(id);

        modelMapper.map(clientUpdateDto, client);
    }

    @Override
    public Page<ClientInfoDto> find(ClientFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }
        Page<Client> page = clientRepository.findAll(whereBuilder(filter), pageable);
        return page.map(client -> modelMapper.map(client, ClientInfoDto.class));
    }

    @Override
    public boolean existsById(long id) {
        return clientRepository.existsByIdAndDeleteDateIsNull(id);
    }


    @Override
    public ClientInfoDto findById(long id) {
        validateExist(id);

        Client client = clientRepository.getOne(id);
        return modelMapper.map(client, ClientInfoDto.class);
    }

    private BooleanBuilder whereBuilder(ClientFilterDto filterDto) {
        BooleanBuilder builder = new BooleanBuilder();

        if (!StringUtils.isEmpty(filterDto.getName())) {
            builder.and(QClient.client.name.startsWithIgnoreCase(filterDto.getName()));
        }
        if (filterDto.getStatus() != null && filterDto.getStatus().size() != 0) {
            builder.and(QClient.client.status.in(filterDto.getStatus()));
        }

        return builder;
    }

    private void updateValidation(long id) {
        if (!clientRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ClientNotFoundException("Company with id %s not found!", id);
        }
    }

    private void deleteValidation(long id) {
        if (!clientRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ClientNotFoundException("Company with id %s not found!", id);
        }
    }

    private void addingValidation(ClientAddDto clientAddDto) {
        if (clientRepository.existsByName(clientAddDto.getName())) {
            throw new DuplicatedClientNameException("Company with the name %s already exists!", clientAddDto);
        }

        if (clientAddDto.getAdminInfo().getBornDate().isAfter(LocalDate.now())) {
            throw new InvalidDateException("Invalid date! %s is more than now", clientAddDto.getAdminInfo().getBornDate());
        }
    }

    private void validateActivate(long id) {
        if (clientRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ClientNotFoundException("Company with id %s not found!", id);
        }
    }

    private void validateExist(long id) {
        if (!clientRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ClientNotFoundException("Company with id %s not found!", id);
        }
    }
}
