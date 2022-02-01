package com.itechart.cargotrucking.core.product.service;

import com.itechart.cargotrucking.core.invoice.dto.InvoiceFullInfoDto;
import com.itechart.cargotrucking.core.invoice.service.InvoiceService;
import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.ProductWriteoff;
import com.itechart.cargotrucking.core.product.QProductWriteoff;
import com.itechart.cargotrucking.core.product.dto.ProductInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductWriteoffUpdateDto;
import com.itechart.cargotrucking.core.product.exception.ProductNotFoundException;
import com.itechart.cargotrucking.core.product.exception.ProductWriteoffNotFoundException;
import com.itechart.cargotrucking.core.product.repository.ProductWriteoffRepository;
import com.querydsl.core.BooleanBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Validated
public class ProductWriteoffServiceImpl implements ProductWriteoffService {
    private InvoiceService invoiceService;
    private ProductWriteoffRepository productWriteoffRepository;
    private ProductService productService;
    private ModelMapper modelMapper;

    @Autowired
    public ProductWriteoffServiceImpl(
            InvoiceService invoiceService,
            ProductWriteoffRepository productWriteoffRepository,
            ProductService productService,
            ModelMapper modelMapper
    ) {
        this.invoiceService = invoiceService;
        this.productWriteoffRepository = productWriteoffRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<ProductWriteoffAddDto, ProductWriteoff>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    @Override
    public long add(ProductWriteoffAddDto productWriteoffAddDto) {
        validateAdd(productWriteoffAddDto);

        ProductWriteoff productWriteoff = modelMapper.map(productWriteoffAddDto, ProductWriteoff.class);
        productWriteoffRepository.save(productWriteoff);

        productService.writeoff(
                productWriteoffAddDto.getProductId(),
                productWriteoffAddDto.getAmount(),
                productWriteoff
        );

        return productWriteoff.getId();
    }

    @Override
    public void update(long id, ProductWriteoffUpdateDto productWriteoffUpdateDto) {
        validateUpdate(id, productWriteoffUpdateDto);

        ProductWriteoff productWriteoff = productWriteoffRepository.getOne(id);
        Product writeoffedProduct = productService.findByProductWriteoffId(id);
        modelMapper.map(productWriteoffUpdateDto, productWriteoff);

        productService.writeoffUpdate(
                productWriteoff.getProductId(),
                productWriteoffUpdateDto.getAmount(),
                writeoffedProduct.getId()
        );
    }

    @Override
    public void delete(long... ids) {
        for (long id : ids) {
            validateDelete(id);
            Product writeoffedProduct = productService.findByProductWriteoffId(id);
            productService.writeoffDelete(productWriteoffRepository.getProductId(id), writeoffedProduct.getId());
            productWriteoffRepository.delete(id);
        }
    }

    @Override
    public List<ProductWriteoffInfoDto> find(long invoiceId) {
        InvoiceFullInfoDto invoice = invoiceService.findInfoById(invoiceId);
        Set<Long> productIds = new HashSet<>();
        for (ProductInfoDto product : invoice.getProducts()) {
            productIds.add(product.getId());
        }

        BooleanBuilder whereBuilder = new BooleanBuilder(QProductWriteoff.productWriteoff.deleteDate.isNull());
        whereBuilder.and(QProductWriteoff.productWriteoff.productId.in(productIds));

        List<ProductWriteoffInfoDto> productWriteoffList = new ArrayList<>();
        for (ProductWriteoff productWriteoff : productWriteoffRepository.findAll(whereBuilder)) {
            ProductWriteoffInfoDto productWriteoffInfo = modelMapper.map(productWriteoff, ProductWriteoffInfoDto.class);

            productWriteoffInfo.setName(productService.getProductNameByWriteoffId(productWriteoff.getId()));
            productWriteoffList.add(productWriteoffInfo);
        }

        return productWriteoffList;
    }

    @Override
    public ProductWriteoff findById(long id) {
        return productWriteoffRepository.getOne(id);
    }

    @Override
    public long getClientId(long id) {
        validateExists(id);

        return productWriteoffRepository.getClientId(id);
    }

    private void validateAdd(ProductWriteoffAddDto productWriteoffAddDto) {
        if (!productService.existsByIdAndAmount(productWriteoffAddDto.getProductId(), productWriteoffAddDto.getAmount())) {
            throw new ProductNotFoundException(
                    "Product with id %s and amount %s doesn't exists",
                    productWriteoffAddDto.getProductId(),
                    productWriteoffAddDto.getAmount()
            );
        }
    }

    private void validateUpdate(long id, ProductWriteoffUpdateDto productWriteoffUpdateDto) {
        if (!productWriteoffRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductWriteoffNotFoundException("Product writeoff with id %s doesn't exists", id);
        }

        Product writeoffedProduct = productService.findByProductWriteoffId(id);
        Product product = productService.findById(productWriteoffRepository.getProductId(id));

        if (productWriteoffUpdateDto.getAmount() > writeoffedProduct.getAmount() + product.getAmount()) {
            throw new ProductNotFoundException(
                    "Product with id %s and amount %s doesn't exists",
                    product.getId(),
                    productWriteoffUpdateDto.getAmount()
            );
        }
    }

    private void validateDelete(long id) {
        if (!productWriteoffRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductWriteoffNotFoundException("Product writeoff with id %s doesn't exists", id);
        }
    }

    private void validateExists(long id) {
        if (!productWriteoffRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductWriteoffNotFoundException("Product writeoff with id %s doesn't exists", id);
        }
    }
}
