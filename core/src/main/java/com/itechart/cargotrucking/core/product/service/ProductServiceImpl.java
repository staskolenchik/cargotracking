package com.itechart.cargotrucking.core.product.service;

import com.itechart.cargotrucking.core.invoice.repository.InvoiceRepository;
import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.ProductStatus;
import com.itechart.cargotrucking.core.product.ProductWriteoff;
import com.itechart.cargotrucking.core.product.QProduct;
import com.itechart.cargotrucking.core.product.dto.ProductAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductInfoDto;
import com.itechart.cargotrucking.core.product.dto.ProductUpdateDto;
import com.itechart.cargotrucking.core.product.exception.ProductNotFoundException;
import com.itechart.cargotrucking.core.product.repository.ProductRepository;
import com.querydsl.core.BooleanBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private InvoiceRepository invoiceRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository,
            InvoiceRepository invoiceRepository,
            ModelMapper modelMapper
    ) {
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public long add(long invoiceId, ProductAddDto productAddDto) {
        Product product = modelMapper.map(productAddDto, Product.class);
        product.setStatus(ProductStatus.ACCEPTED);
        product.setInvoice(invoiceRepository.getOne(invoiceId));

        productRepository.save(product);
        return product.getId();
    }

    @Override
    public void update(long id, ProductUpdateDto productUpdateDto) {
        validateUpdate(id);

        Product product = productRepository.getOne(id);

        modelMapper.map(productUpdateDto, product);
    }

    @Override
    public void delete(long id) {
        validateDelete(id);
        productRepository.delete(id);
    }

    @Override
    public boolean existsByIdAndAmount(long id, short amount) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QProduct.product.id.eq(id));
        whereBuilder.and(QProduct.product.deleteDate.isNull());
        whereBuilder.and(QProduct.product.amount.goe(amount));
        whereBuilder.and(QProduct.product.status.eq(ProductStatus.DELIVERED));

        return productRepository.exists(whereBuilder);
    }

    @Override
    public void writeoff(long id, short amount, ProductWriteoff writeoff) {
        validateUpdate(id);

        Product product = productRepository.getOne(id);
        product.setAmount((short) (product.getAmount() - amount));

        Product writeoffProduct = new Product();
        writeoffProduct.setStatus(ProductStatus.LOST);
        writeoffProduct.setAmount(amount);
        writeoffProduct.setInvoice(product.getInvoice());
        writeoffProduct.setName(product.getName());
        writeoffProduct.setProductWriteoff(writeoff);

        productRepository.save(writeoffProduct);
    }

    @Override
    public void writeoffUpdate(long id, short amount, long writeoffedProductId) {
        validateUpdate(id);

        Product product = productRepository.getOne(id);
        Product writeoffProduct = productRepository.getOne(writeoffedProductId);

        short generalAmount = (short) (product.getAmount() + writeoffProduct.getAmount());

        product.setAmount((short) (generalAmount - amount));
        writeoffProduct.setAmount(amount);
    }

    @Override
    public void writeoffDelete(long id, long writeoffedProductId) {
        validateDelete(id);

        Product product = productRepository.getOne(id);
        short writeoffProductAmount = productRepository.getProductAmountById(writeoffedProductId);

        product.setAmount((short) (product.getAmount() + writeoffProductAmount));
        productRepository.delete(writeoffedProductId);
    }

    @Override
    public List<ProductInfoDto> findByInvoiceId(long invoiceId) {
        List<Product> products = productRepository.findNotLostByInvoiceId(invoiceId);

        return products.stream().map(product -> modelMapper.map(product, ProductInfoDto.class)).collect(Collectors.toList());
    }

    @Override
    public Product findByProductWriteoffId(long writeoffId) {
        validateWriteoffedExists(writeoffId);

        return productRepository.findByProductWriteoffId(writeoffId);
    }

    @Override
    public Product findById(long id) {
        return productRepository.getOne(id);
    }

    @Override
    public String getProductNameByWriteoffId(long writeoffId) {
        return productRepository.getProductNameByWriteoffId(writeoffId);
    }

    @Override
    public long getClientId(long id) {
        validateExists(id);

        return productRepository.getClientId(id);
    }

    private void validateUpdate(long id) {
        if (!productRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductNotFoundException("Product with id %s doesn't exists", id);
        }
    }

    private void validateDelete(long id) {
        if (!productRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductNotFoundException("Product with id %s doesn't exists", id);
        }
    }

    private void validateExists(long id) {
        if (!productRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductNotFoundException("Product with id %s doesn't exists", id);
        }
    }

    private void validateWriteoffedExists(long writeoffId) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QProduct.product.productWriteoff.id.eq(writeoffId));
        whereBuilder.and(QProduct.product.deleteDate.isNull());
        whereBuilder.and(QProduct.product.status.eq(ProductStatus.LOST));

        if (!productRepository.exists(whereBuilder)) {
            throw new ProductNotFoundException("Product with writeoff id %s doesn't exists", writeoffId);
        }
    }
}
