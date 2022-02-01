package com.itechart.cargotrucking.core.storage.service;

import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.core.solr.storage.StorageSolr;
import com.itechart.cargotrucking.core.solr.storage.StorageSolrRepository;
import com.itechart.cargotrucking.core.storage.Storage;
import com.itechart.cargotrucking.core.storage.dto.StorageAddDto;
import com.itechart.cargotrucking.core.storage.dto.StorageFilterDto;
import com.itechart.cargotrucking.core.storage.dto.StorageInfoDto;
import com.itechart.cargotrucking.core.storage.dto.StorageUpdateDto;
import com.itechart.cargotrucking.core.storage.exception.StorageNotFoundException;
import com.itechart.cargotrucking.core.storage.repository.StorageRepository;
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
public class StorageServiceImpl implements StorageService{
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private StorageRepository storageRepository;
    private StorageSolrRepository storageSolrRepository;
    private ClientService clientService;
    private ModelMapper modelMapper;

    @Autowired
    public StorageServiceImpl(
            StorageRepository storageRepository,
            StorageSolrRepository storageSolrRepository,
            ClientService clientService,
            ModelMapper modelMapper
    ) {
        this.storageRepository = storageRepository;
        this.storageSolrRepository = storageSolrRepository;
        this.clientService = clientService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<StorageAddDto, Storage>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    @Override
    public long add(StorageAddDto storageAddDto) {
        validateAdd(storageAddDto);
        Storage storage = modelMapper.map(storageAddDto, Storage.class);

        storageRepository.save(storage);
        storageSolrRepository.save(modelMapper.map(storage, StorageSolr.class));

        return storage.getId();
    }

    @Override
    public void update(long id, StorageUpdateDto storageUpdateDto) {
        validateExist(id);
        Storage storage = storageRepository.getOne(id);
        modelMapper.map(storageUpdateDto, storage);

        storageSolrRepository.save(modelMapper.map(storage, StorageSolr.class));
    }

    @Override
    public void delete(long... ids) {
        for(long id : ids){
            validateExist(id);
            storageRepository.delete(id);
            storageSolrRepository.deleteById(id);
        }
    }

    @Override
    public Page<StorageInfoDto> find(long clientId, StorageFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }

        Page<StorageSolr> page = storageSolrRepository.findByClientId(clientId, filter.getName(), pageable);
        return page.map(storage -> modelMapper.map(storage, StorageInfoDto.class));
    }

    @Override
    public StorageInfoDto findById(long id) {
        validateExist(id);
        Storage storage = storageRepository.getOne(id);
        return modelMapper.map(storage, StorageInfoDto.class);
    }

    @Override
    public Long getClientId(long id) {
        validateExist(id);

        return storageRepository.getClientId(id);
    }

    @Override
    public boolean existsById(long id) {
        return storageRepository.existsById(id);
    }

    private void validateAdd(StorageAddDto storageAddDto) {
        if (!clientService.existsById(storageAddDto.getClientId())) {
            throw new ClientNotFoundException("Storage with id %s does not exist", storageAddDto.getClientId());
        }
    }

    private void validateExist(long id) {
        if (!storageRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new StorageNotFoundException("Storage with id %s does not exist", id);
        }
    }
}
