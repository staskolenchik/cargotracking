package com.itechart.cargotrucking.webapp.productowner;

import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerAddDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerFilterDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerInfoDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerUpdateDto;
import com.itechart.cargotrucking.core.productowner.service.ProductOwnerService;
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
@RequestMapping("/api/product-owners")
public class ProductOwnerController {
    private ProductOwnerService productOwnerService;

    @Autowired
    public ProductOwnerController(ProductOwnerService productOwnerService) {
        this.productOwnerService = productOwnerService;
    }

    @PostMapping
    public ResponseEntity<String> addProductOwner(
            @RequestBody @Valid ProductOwnerAddDto productOwner,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        productOwner.setClientId(userCredentials.getClientId());
        long id = productOwnerService.add(productOwner);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateProductOwner(
            @PathVariable long id,
            @RequestBody @Valid ProductOwnerUpdateDto productOwner,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        long clientId = productOwnerService.getClientId(id);

        if (userCredentials.getClientId() != clientId) {
            throw new AccessException(
                    "Authenticated user haven't privileges to update product owners of company with id %s",
                    clientId
            );
        }

        productOwnerService.update(id, productOwner);
    }

    @DeleteMapping
    public void deleteProductOwner(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        for (long id : ids) {
            long clientId = productOwnerService.getClientId(id);

            if (userCredentials.getClientId() != clientId) {
                throw new AccessException(
                        "Authenticated user haven't privileges to delete product owners of company with id %s",
                        clientId
                );
            }
        }

        productOwnerService.delete(ids);
    }

    @GetMapping
    public Page<ProductOwnerInfoDto> productOwnerListByFilters(
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable,
            ProductOwnerFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return productOwnerService.find(userCredentials.getClientId(), filter, pageable);
    }

    @GetMapping("/{id}")
    public ProductOwnerInfoDto productOwnerById(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        long clientId = productOwnerService.getClientId(id);

        if (userCredentials.getClientId() != clientId) {
            throw new AccessException(
                    "Authenticated user haven't privileges to see product owners of company with id %s",
                    clientId
            );
        }

        return productOwnerService.findById(id);
    }
}
