package com.itechart.cargotrucking.webapp.client;

import com.itechart.cargotrucking.core.client.dto.ClientAddDto;
import com.itechart.cargotrucking.core.client.dto.ClientFilterDto;
import com.itechart.cargotrucking.core.client.dto.ClientInfoDto;
import com.itechart.cargotrucking.core.client.dto.ClientUpdateDto;
import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.webapp.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private ClientService clientService;


    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<String> addClient(@Valid @RequestBody ClientAddDto client) {

        String encryptPassword = UserController.passwordEncryption(client.getAdminInfo().getPassword());
        client.getAdminInfo().setPassword(encryptPassword);
        client.getAdminInfo().setPasswordConfirm(encryptPassword);

        long id = clientService.add(client);


        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateClient(@PathVariable long id, @RequestBody @Valid ClientUpdateDto client) {
        clientService.update(id, client);
    }

    @PutMapping("/activate/{id}")
    public void activateClient(@PathVariable long id) {
        clientService.activate(id);
    }

    @DeleteMapping
    public void inactivateClient(@RequestBody long... ids) {
        clientService.inactivate(ids);
    }

    @GetMapping
    public Page<ClientInfoDto> clientListWithFilters(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable,
            ClientFilterDto filter
    ) {
        return clientService.find(filter, pageable);
    }

    @GetMapping("/{id}")
    public ClientInfoDto clientById(@PathVariable long id) {
        return clientService.findById(id);
    }
}
