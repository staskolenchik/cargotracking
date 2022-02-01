package com.itechart.cargotrucking.core.storage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class StorageAddDto {
    @NotBlank(message = "Storage name cannot be empty")
    @Length(max = 20, message = "Storage name length cannot be more than 20 characters")
    private String name;

    @NotBlank(message = "Storage address cannot be empty")
    @Length(max = 40, message = "Storage address length cannot be more than 40 characters")
    private String address;

    private Long clientId;
}
