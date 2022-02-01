package com.itechart.cargotrucking.webapp.invoice;

import com.itechart.cargotrucking.core.invoice.InvoiceStatus;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceAddDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFilterDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFullInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceUpdateDto;
import com.itechart.cargotrucking.core.invoice.service.InvoiceService;
import com.itechart.cargotrucking.core.productowner.service.ProductOwnerService;
import com.itechart.cargotrucking.core.storage.service.StorageService;
import com.itechart.cargotrucking.core.user.service.UserService;
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
@RequestMapping("/api/invoices")
public class InvoiceController {
    private InvoiceService invoiceService;
    private StorageService storageService;
    private ProductOwnerService productOwnerService;
    private UserService userService;

    @Autowired
    public InvoiceController(
            InvoiceService invoiceService,
            StorageService storageService,
            ProductOwnerService productOwnerService,
            UserService userService
    ) {
        this.invoiceService = invoiceService;
        this.storageService = storageService;
        this.productOwnerService = productOwnerService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> addInvoice(@Valid @RequestBody InvoiceAddDto invoice, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long driverClientId = userService.getClientId(invoice.getDriverId());
        Long storageClientId = storageService.getClientId(invoice.getStorageId());
        Long productOwnerId = productOwnerService.getClientId(invoice.getProductOwnerId());

        if (!userCredentials.getClientId().equals(driverClientId)
                || !userCredentials.getClientId().equals(storageClientId)
                || !userCredentials.getClientId().equals(productOwnerId)
        ) {
            throw new AccessException("Authenticated user haven't privileges to use drivers, " +
                    "storages or product owners from another companies");
        }

        invoice.setCreatorId(userCredentials.getUserId());
        long id = invoiceService.add(invoice);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateInvoice(@PathVariable long id, @Valid @RequestBody InvoiceUpdateDto invoice, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = invoiceService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to update invoices of company with id %s", clientId);
        }

        Long driverClientId = userService.getClientId(invoice.getDriverId());
        Long storageClientId = storageService.getClientId(invoice.getStorageId());
        Long productOwnerId = productOwnerService.getClientId(invoice.getProductOwnerId());

        if (!userCredentials.getClientId().equals(driverClientId)
                || !userCredentials.getClientId().equals(storageClientId)
                || !userCredentials.getClientId().equals(productOwnerId)
        ) {
            throw new AccessException("Authenticated user haven't privileges to use drivers, " +
                    "storages or product owners from another companies");
        }

        invoiceService.update(id, invoice);
    }

    @DeleteMapping
    public void deleteInvoice(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        for (long id : ids) {
            Long clientId = invoiceService.getClientId(id);

            if (!userCredentials.getClientId().equals(clientId)) {
                throw new AccessException("Authenticated user haven't privileges to delete invoices of company with id %s", clientId);
            }
        }

        invoiceService.delete(ids);
    }

    @GetMapping
    public Page<InvoiceInfoDto> invoiceListByFilters(
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable,
            InvoiceFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        if (userService.existsByIdAndDispatcherRole(userCredentials.getUserId())
            || userService.existsByIdAndManagerRole(userCredentials.getUserId())
        ) {
            if (filter.getStatuses() == null) {
                filter.setStatuses(new InvoiceStatus[0]);
            }
            filter.getStatuses().add(InvoiceStatus.MADE_OUT);
            filter.getStatuses().add(InvoiceStatus.DELIVERED);
        }

        return invoiceService.find(userCredentials.getClientId(), filter, pageable);
    }

    @GetMapping("/{id}")
    public InvoiceFullInfoDto invoiceById(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = invoiceService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to see invoices of company with id %s", clientId);
        }

        return invoiceService.findInfoById(id);
    }
}
