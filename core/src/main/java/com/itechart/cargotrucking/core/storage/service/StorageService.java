package com.itechart.cargotrucking.core.storage.service;

import com.itechart.cargotrucking.core.storage.dto.StorageAddDto;
import com.itechart.cargotrucking.core.storage.dto.StorageFilterDto;
import com.itechart.cargotrucking.core.storage.dto.StorageInfoDto;
import com.itechart.cargotrucking.core.storage.dto.StorageUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface StorageService {
    @Transactional
    long add(StorageAddDto storage);

    @Transactional
    void update(long id, StorageUpdateDto storage);

    @Transactional
    void delete(long... ids);

    Page<StorageInfoDto> find(long clientId, StorageFilterDto filter, Pageable pageable);

    StorageInfoDto findById(long id);

    boolean existsById(long id);

    Long getClientId(long id);
}
