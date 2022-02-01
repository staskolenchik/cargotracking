package com.itechart.cargotrucking.webapp.storage;

import com.itechart.cargotrucking.core.storage.dto.StorageAddDto;
import com.itechart.cargotrucking.core.storage.dto.StorageFilterDto;
import com.itechart.cargotrucking.core.storage.dto.StorageInfoDto;
import com.itechart.cargotrucking.core.storage.dto.StorageUpdateDto;
import com.itechart.cargotrucking.core.storage.service.StorageService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/storages")
public class StorageController {
    private StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService){
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<String> addStorage(@Valid @RequestBody StorageAddDto storage, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        storage.setClientId(userCredentials.getClientId());
        long id = storageService.add(storage);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateStorage(@PathVariable long id, @Valid @RequestBody StorageUpdateDto storage, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = storageService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to update storages of company with id %s", clientId);
        }

        storageService.update(id, storage);
    }

    @DeleteMapping
    public void deleteStorage(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        for (long id : ids) {
            Long clientId = storageService.getClientId(id);

            if (!userCredentials.getClientId().equals(clientId)) {
                throw new AccessException("Authenticated user haven't privileges to delete storages of company with id %s", clientId);
            }
        }

        storageService.delete(ids);
    }

    @GetMapping("/{id}")
    public StorageInfoDto storageById(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = storageService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to update storages of company with id %s", clientId);
        }

        return storageService.findById(id);
    }

    @GetMapping
    public Page<StorageInfoDto> storageListByFilters(
            @PageableDefault(sort = { "name" }, direction = Sort.Direction.ASC) Pageable pageable,
            StorageFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return storageService.find(userCredentials.getClientId(), filter, pageable);
    }
}
