package com.itechart.cargotrucking.core.email;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailMessage {
    @NotBlank(message = "Message recipient cannot be empty")
    private String recipient;

    private String subject;

    @NotBlank(message = "Message text cannot be empty")
    private String text;
}
