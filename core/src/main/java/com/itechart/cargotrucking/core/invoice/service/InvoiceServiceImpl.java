package com.itechart.cargotrucking.core.invoice.service;

import com.itechart.cargotrucking.core.invoice.Invoice;
import com.itechart.cargotrucking.core.invoice.InvoiceStatus;
import com.itechart.cargotrucking.core.invoice.QInvoice;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceAddDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFilterDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceFullInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceInfoDto;
import com.itechart.cargotrucking.core.invoice.dto.InvoiceUpdateDto;
import com.itechart.cargotrucking.core.invoice.exception.EmptyProductListException;
import com.itechart.cargotrucking.core.invoice.exception.InvoiceNotFoundException;
import com.itechart.cargotrucking.core.invoice.repository.InvoiceRepository;
import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.dto.ProductAddDto;
import com.itechart.cargotrucking.core.product.dto.ProductUpdateDto;
import com.itechart.cargotrucking.core.product.exception.ProductNotFoundException;
import com.itechart.cargotrucking.core.product.service.ProductService;
import com.itechart.cargotrucking.core.productowner.ProductOwner;
import com.itechart.cargotrucking.core.productowner.exception.ProductOwnerNotFoundException;
import com.itechart.cargotrucking.core.productowner.service.ProductOwnerService;
import com.itechart.cargotrucking.core.storage.Storage;
import com.itechart.cargotrucking.core.storage.exception.StorageNotFoundException;
import com.itechart.cargotrucking.core.storage.service.StorageService;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

@Service
@Validated
public class InvoiceServiceImpl implements InvoiceService {
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private InvoiceRepository invoiceRepository;
    private ProductService productService;
    private StorageService storageService;
    private UserService userService;
    private ProductOwnerService productOwnerService;
    private ModelMapper modelMapper;

    @Autowired
    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            ProductService productService,
            StorageService storageService,
            UserService userService,
            ProductOwnerService productOwnerService,
            ModelMapper modelMapper
    ) {
        this.invoiceRepository = invoiceRepository;
        this.productService = productService;
        this.storageService = storageService;
        this.userService = userService;
        this.productOwnerService = productOwnerService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<InvoiceUpdateDto, Invoice>() {
            @Override
            protected void configure() {
                skip(destination.getProducts());
            }
        });
        this.modelMapper.addMappings(new PropertyMap<InvoiceAddDto, Invoice>() {
            @Override
            protected void configure() {
                skip(destination.getProducts());
            }
        });
    }

    @Override
    public long add(InvoiceAddDto invoiceAddDto) {
        validateAdd(invoiceAddDto);

        Invoice invoice = modelMapper.map(invoiceAddDto, Invoice.class);

        invoice.setCreationDate(LocalDate.now());
        invoice.setStatus(InvoiceStatus.MADE_OUT);



        invoiceRepository.save(invoice);
        for (ProductAddDto productAddDto : invoiceAddDto.getProducts()) {
            productService.add(invoice.getId(), productAddDto);
        }

        return invoice.getId();
    }

    @Override
    public void update(long id, InvoiceUpdateDto invoiceUpdateDto) {
        validateUpdate(id, invoiceUpdateDto);

        Invoice invoice = invoiceRepository.getOne(id);

        invoice.setProductOwner(new ProductOwner());
        invoice.setStorage(new Storage());
        invoice.setDriver(new User());

        modelMapper.map(invoiceUpdateDto, invoice);

        for (Product product : invoice.getProducts()) {
            boolean deleted = true;
            for (ProductUpdateDto productUpdateDto : invoiceUpdateDto.getProducts()) {
                if (productUpdateDto.getId() != null && productUpdateDto.getId().equals(product.getId())) {
                    deleted = false;
                    break;
                }
            }
            if (deleted) {
                productService.delete(product.getId());
            }
        }

        for (ProductUpdateDto productDto : invoiceUpdateDto.getProducts()) {
            if (productDto.getId() != null) {
                boolean exists = false;
                for (Product product : invoice.getProducts()) {
                    if (product.getId().equals(productDto.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    throw new ProductNotFoundException(
                            "Product with id %s and invoice id %s doesn't exists",
                            productDto.getId(), id
                    );
                }

                productService.update(productDto.getId(), productDto);
            } else {
                productService.add(id, modelMapper.map(productDto, ProductAddDto.class));
            }
        }
    }

    @Override
    public void delete(long... ids) {
        for (long id : ids) {
            validateDelete(id);
            invoiceRepository.delete(id);
        }
    }

    @Override
    public boolean existsById(long id) {
        return invoiceRepository.existsByIdAndDeleteDateIsNull(id);
    }

    @Override
    public Page<InvoiceInfoDto> find(long clientId, InvoiceFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }

        Page<Invoice> page = invoiceRepository.findAll(buildWhere(clientId, filter), pageable);
        return page.map(invoice -> modelMapper.map(invoice, InvoiceInfoDto.class));
    }

    @Override
    public Long getClientId(long id) {
        validateExist(id);

        return invoiceRepository.getClientId(id);
    }

    @Override
    public InvoiceFullInfoDto findInfoById(long id) {
        validateExist(id);

        Invoice invoice = invoiceRepository.getOne(id);

        InvoiceFullInfoDto fullInfo = modelMapper.map(invoice, InvoiceFullInfoDto.class);
        fullInfo.getProducts().clear();
        fullInfo.getProducts().addAll(productService.findByInvoiceId(id));

        return fullInfo;
    }

    private void validateAdd(InvoiceAddDto invoice) {
        if (!storageService.existsById(invoice.getStorageId())) {
            throw new StorageNotFoundException("Storage with id %s doesn't exists", invoice.getStorageId());
        }

        if (!productOwnerService.existsById(invoice.getProductOwnerId())) {
            throw new ProductOwnerNotFoundException("Product owner with id %s doesn't exists", invoice.getProductOwnerId());
        }

        if (!userService.existsByIdAndDriverRole(invoice.getDriverId())) {
            throw new UserNotFoundException("Driver with id %s doesn't exists", invoice.getDriverId());
        }

        if (invoice.getProducts().size() == 0) {
            throw new EmptyProductListException();
        }
    }

    private void validateUpdate(long id, InvoiceUpdateDto invoiceUpdateDto) {
        if (!invoiceRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new InvoiceNotFoundException("Invoice with id %s doesn't exists", id);
        }

        String invoiceNumber = invoiceRepository.getOne(id).getNumber();
    }

    private void validateDelete(long id) {
        if (!invoiceRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new InvoiceNotFoundException("Invoice with id %s doesn't exists", id);
        }
    }

    private void validateExist(long id) {
        if (!invoiceRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new InvoiceNotFoundException("Invoice with id %s doesn't exists", id);
        }
    }

    private BooleanBuilder buildWhere(long clientId, InvoiceFilterDto filter) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QInvoice.invoice.deleteDate.isNull());
        whereBuilder.and(QInvoice.invoice.creator.clientId.eq(clientId));

        if (!StringUtils.isEmpty(filter.getNumber())) {
            whereBuilder.and(QInvoice.invoice.number.contains(filter.getNumber()));
        }

        if (filter.getAfterCreationDate() != null) {
            whereBuilder.and(QInvoice.invoice.creationDate.after(filter.getAfterCreationDate()));
        }
        if (filter.getBeforeCreationDate() != null) {
            whereBuilder.and(QInvoice.invoice.creationDate.before(filter.getBeforeCreationDate()));
        }

        if (filter.getAfterVerifiedDate() != null) {
            whereBuilder.and(QInvoice.invoice.verifiedDate.after(filter.getAfterVerifiedDate()));
        }
        if (filter.getBeforeVerifiedDate() != null) {
            whereBuilder.and(QInvoice.invoice.verifiedDate.before(filter.getBeforeVerifiedDate()));
        }

        if (filter.getStatuses() != null && filter.getStatuses().size() != 0) {
            whereBuilder.and(QInvoice.invoice.status.in(filter.getStatuses()));
        }

        return whereBuilder;
    }
}
