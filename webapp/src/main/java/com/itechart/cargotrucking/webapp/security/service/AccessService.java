package com.itechart.cargotrucking.webapp.security.service;

import com.itechart.cargotrucking.webapp.security.dto.LoginDto;
import com.itechart.cargotrucking.webapp.security.dto.LogoutDto;
import com.itechart.cargotrucking.webapp.security.dto.RefreshDto;
import org.springframework.security.core.Authentication;

public interface AccessService {
    String signin(LoginDto loginDto);

    void logout(LogoutDto logoutDto);

    String refreshAccessToken(RefreshDto refreshDto);
}
