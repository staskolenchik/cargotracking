package com.itechart.cargotrucking.core.client.dto;

import com.itechart.cargotrucking.core.client.ClientType;
import com.itechart.cargotrucking.core.user.dto.UserAddDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ClientAddDto {
    @NotBlank(message = "Client name cannot be empty")
    @Length(max = 30, message = "Client name length cannot be more than 30 characters")
    private String name;

    @NotNull(message = "Client status cannot be empty")
    private ClientType status;

    @NotNull(message = "Client admin info cannot be empty")
    private UserAddDto adminInfo;
}
