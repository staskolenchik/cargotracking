package com.itechart.cargotrucking.webapp.productwriteoff;

import com.itechart.cargotrucking.core.invoice.exception.InvoiceNotFoundException;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffUpdateDto;
import com.itechart.cargotrucking.core.product.service.ProductService;
import com.itechart.cargotrucking.core.product.service.ProductWriteoffService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@RestController
@RequestMapping("/api/product-writeoffs")
public class ProductWriteoffController {
    private ProductWriteoffService productWriteoffService;
    private ProductService productService;

    @Autowired
    public ProductWriteoffController(ProductWriteoffService productWriteoffService, ProductService productService) {
        this.productWriteoffService = productWriteoffService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> addProductWriteoff(
            @Valid @RequestBody ProductWriteoffAddDto productWriteoffAddDto,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        long clientId = productService.getClientId(productWriteoffAddDto.getProductId());
        if (clientId != userCredentials.getClientId()) {
            throw new AccessException(
                    "Authenticated user haven't privileges to update products of company with id %s",
                    clientId
            );
        }

        productWriteoffAddDto.setCreatorId(userCredentials.getUserId());
        long id = productWriteoffService.add(productWriteoffAddDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public void updateProductWriteoff(
            @PathVariable long id,
            @Valid @RequestBody ProductWriteoffUpdateDto productWriteoffUpdateDto,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        long clientId = productWriteoffService.getClientId(id);
        if (clientId != userCredentials.getClientId()) {
            throw new AccessException(
                    "Authenticated user haven't privileges to update products of company with id %s",
                    clientId
            );
        }

        productWriteoffService.update(id, productWriteoffUpdateDto);
    }

    @DeleteMapping
    public void deleteProductWriteoff(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        for (long id : ids) {
            long clientId = productWriteoffService.getClientId(id);

            if (userCredentials.getClientId() != clientId) {
                throw new AccessException(
                        "Authenticated user haven't privileges to delete product writeoff of company with id %s",
                        clientId
                );
            }
        }

        productWriteoffService.delete(ids);
    }

    @GetMapping
    public List<ProductWriteoffInfoDto> findProductWriteoffList(Long invoiceId) {
        if (invoiceId == null) {
            throw new InvoiceNotFoundException("Invoice with null id doesn't exists");
        }

        return productWriteoffService.find(invoiceId);
    }
}
