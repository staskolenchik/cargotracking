package com.itechart.cargotrucking.core.security;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class RefreshToken {
    @EmbeddedId
    private RefreshTokenKey id;

    @Column(name = "token")
    private String token;

    public RefreshToken() {
    }

    public RefreshToken(long userId, String ip, String token) {
        id = new RefreshTokenKey();
        id.setUserId(userId);
        id.setIp(ip);
        this.token = token;
    }
}
