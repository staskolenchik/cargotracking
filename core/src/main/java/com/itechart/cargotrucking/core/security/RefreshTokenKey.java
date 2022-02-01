package com.itechart.cargotrucking.core.security;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
public class RefreshTokenKey implements Serializable {
    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @NotBlank
    @Column(name = "ip", nullable = false, updatable = false)
    private String ip;

    public RefreshTokenKey() {
    }

    public RefreshTokenKey(Long userId, String ip) {
        this.userId = userId;
        this.ip = ip;
    }
}
