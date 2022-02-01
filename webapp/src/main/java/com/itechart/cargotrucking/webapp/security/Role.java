package com.itechart.cargotrucking.webapp.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    SYS_ADMIN,
    ADMIN,
    DISPATCHER,
    MANAGER,
    DRIVER,
    COMPANY_OWNER;

    @Override
    public String getAuthority() {
        return name();
    }
}
