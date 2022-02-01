package com.itechart.cargotrucking.core.security.service;

import com.itechart.cargotrucking.core.security.RefreshToken;
import com.itechart.cargotrucking.core.security.RefreshTokenKey;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {
    @Transactional
    void add(RefreshToken refreshToken);

    @Transactional
    void update(RefreshToken refreshToken);

    @Transactional
    void delete(RefreshTokenKey... ids);

    RefreshToken find(RefreshTokenKey id);
}
