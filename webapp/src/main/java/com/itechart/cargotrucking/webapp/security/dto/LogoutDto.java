package com.itechart.cargotrucking.webapp.security.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class LogoutDto {
    @NotNull(message = "User id cannot be empty")
    private Long userId;

    @NotBlank(message = "IP cannot be empty")
    @Pattern(regexp = "([0-9]{1,3}[.]){3}[0-9]{1,3}", message = "IP does not match regexp")
    private String ip;
}
