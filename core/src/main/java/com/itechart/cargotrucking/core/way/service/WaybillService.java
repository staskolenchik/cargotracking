package com.itechart.cargotrucking.core.way.service;

import com.itechart.cargotrucking.core.way.Waybill;
import com.itechart.cargotrucking.core.way.dto.CheckpointInfoDto;
import com.itechart.cargotrucking.core.way.dto.WaybillAddDto;
import com.itechart.cargotrucking.core.way.dto.WaybillFilterDto;
import com.itechart.cargotrucking.core.way.dto.WaybillInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WaybillService {
    Page<WaybillInfoDto> find(Long userId, Long clientId, WaybillFilterDto filter, Pageable pageable);

    List<Waybill> findByClientId(long clientId);

    List<CheckpointInfoDto> findCheckpoints(long waybillId);

    @Transactional
    void reachCheckpoint(long waybillId, long userId);

    @Transactional
    long createWaybill(WaybillAddDto waybillAddDto);

    @Transactional
    void deleteWaybill(long[] ids);
}
