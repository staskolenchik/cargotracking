package com.itechart.cargotrucking.webapp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCredentials {
    private Long clientId;
    private long userId;
}
