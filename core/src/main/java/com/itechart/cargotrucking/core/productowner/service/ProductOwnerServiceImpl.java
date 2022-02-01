package com.itechart.cargotrucking.core.productowner.service;

import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.core.productowner.ProductOwner;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerAddDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerFilterDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerInfoDto;
import com.itechart.cargotrucking.core.productowner.dto.ProductOwnerUpdateDto;
import com.itechart.cargotrucking.core.productowner.exception.ProductOwnerNotFoundException;
import com.itechart.cargotrucking.core.productowner.repository.ProductOwnerRepository;
import com.itechart.cargotrucking.core.solr.productowner.ProductOwnerSolr;
import com.itechart.cargotrucking.core.solr.productowner.ProductOwnerSolrRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProductOwnerServiceImpl implements ProductOwnerService {
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private ProductOwnerRepository productOwnerRepository;
    private ProductOwnerSolrRepository productOwnerSolrRepository;
    private ClientService clientService;
    private ModelMapper modelMapper;

    @Autowired
    public ProductOwnerServiceImpl(
            ProductOwnerRepository productOwnerRepository,
            ProductOwnerSolrRepository productOwnerSolrRepository,
            ClientService clientService,
            ModelMapper modelMapper
    ) {
        this.productOwnerRepository = productOwnerRepository;
        this.productOwnerSolrRepository = productOwnerSolrRepository;
        this.clientService = clientService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<ProductOwnerAddDto, ProductOwner>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    @Override
    public long add(ProductOwnerAddDto productOwnerAddDto) {
        validateAdd(productOwnerAddDto);
        ProductOwner productOwner = modelMapper.map(productOwnerAddDto, ProductOwner.class);

        productOwnerRepository.save(productOwner);
        productOwnerSolrRepository.save(modelMapper.map(productOwner, ProductOwnerSolr.class));

        return productOwner.getId();
    }

    @Override
    public void update(long id, ProductOwnerUpdateDto productOwnerUpdateDto) {
        validateUpdate(id);
        ProductOwner productOwner = productOwnerRepository.getOne(id);
        modelMapper.map(productOwnerUpdateDto, productOwner);

        productOwnerSolrRepository.save(modelMapper.map(productOwner, ProductOwnerSolr.class));
    }

    @Override
    public void delete(long... ids) {
        for (long id : ids) {
            validateDelete(id);
            productOwnerRepository.delete(id);
            productOwnerSolrRepository.deleteById(id);
        }
    }

    @Override
    public Page<ProductOwnerInfoDto> find(long clientId, ProductOwnerFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }

        Page<ProductOwnerSolr> page = productOwnerSolrRepository.findByClientId(clientId, filter.getName(), pageable);
        return page.map(productOwner -> modelMapper.map(productOwner, ProductOwnerInfoDto.class));
    }

    @Override
    public Long getClientId(long id) {
        validateExist(id);

        return productOwnerRepository.getClientId(id);
    }

    @Override
    public ProductOwnerInfoDto findById(long id) {
        validateExist(id);

        ProductOwner productOwner = productOwnerRepository.getOne(id);
        return modelMapper.map(productOwner, ProductOwnerInfoDto.class);
    }

    @Override
    public boolean existsById(long id) {
        return productOwnerRepository.existsByIdAndDeleteDateIsNull(id);
    }

    private void validateAdd(ProductOwnerAddDto productOwner) {
        if (!clientService.existsById(productOwner.getClientId())) {
            throw new ClientNotFoundException("Client with id %s doesn't exists", productOwner.getClientId());
        }
    }

    private void validateUpdate(long id) {
        if (!productOwnerRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductOwnerNotFoundException("Product owner with id %s doesn't exists", id);
        }
    }

    private void validateDelete(long id) {
        if (!productOwnerRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductOwnerNotFoundException("Product owner with id %s doesn't exists", id);
        }
    }

    private void validateExist(long id) {
        if (!productOwnerRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new ProductOwnerNotFoundException("Product owner with id %s doesn't exists", id);
        }
    }
}
