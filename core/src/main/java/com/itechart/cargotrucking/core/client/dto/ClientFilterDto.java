package com.itechart.cargotrucking.core.client.dto;

import com.itechart.cargotrucking.core.client.ClientType;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ClientFilterDto {
    private String name;
    private List<ClientType> status;

    public void setStatus(ClientType[] status) {
        if (status != null) {
            this.status = Arrays.asList(status);
        }
    }
}
