package com.itechart.cargotrucking.core.storage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StorageInfoDto {
    private long id;
    private String name;
    private String address;
}
