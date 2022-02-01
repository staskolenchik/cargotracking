package com.itechart.cargotrucking.webapp.security;

import com.itechart.cargotrucking.webapp.security.dto.LoginDto;
import com.itechart.cargotrucking.webapp.security.dto.LogoutDto;
import com.itechart.cargotrucking.webapp.security.dto.RefreshDto;
import com.itechart.cargotrucking.webapp.security.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class SecurityController {
    private AccessService accessService;

    @Autowired
    public SecurityController(AccessService accessService) {
        this.accessService = accessService;
    }

    @PostMapping("/sign-in")
    public String signin(@RequestBody @Valid LoginDto loginDto) {
        return accessService.signin(loginDto);
    }

    @PostMapping("/refresh")
    public String refresh(@RequestBody @Valid RefreshDto refreshDto) {
        return accessService.refreshAccessToken(refreshDto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody @Valid LogoutDto logoutDto) {
        accessService.logout(logoutDto);
    }
}
