package com.itechart.cargotrucking.core.repair.service;

import org.springframework.transaction.annotation.Transactional;

public interface RepairAccountService {
    @Transactional
    void changePassword(String uuid, String password);
}
