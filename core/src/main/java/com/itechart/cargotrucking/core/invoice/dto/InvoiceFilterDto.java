package com.itechart.cargotrucking.core.invoice.dto;

import com.itechart.cargotrucking.core.invoice.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class InvoiceFilterDto {
    private String number;
    private LocalDate beforeCreationDate;
    private LocalDate afterCreationDate;
    private LocalDate beforeVerifiedDate;
    private LocalDate afterVerifiedDate;
    private Set<InvoiceStatus> statuses;

    public void setStatuses(InvoiceStatus[] statuses) {
        if (statuses != null) {
            this.statuses = new HashSet<>(Arrays.asList(statuses));
        }
    }

    public void setBeforeCreationDate(String beforeCreationDate) {
        if (!StringUtils.isEmpty(beforeCreationDate)) {
            this.beforeCreationDate = LocalDate.parse(beforeCreationDate, DateTimeFormatter.ISO_DATE);
        }
    }

    public void setAfterCreationDate(String afterCreationDate) {
        if (!StringUtils.isEmpty(afterCreationDate)) {
            this.afterCreationDate = LocalDate.parse(afterCreationDate, DateTimeFormatter.ISO_DATE);
        }
    }

    public void setBeforeVerifiedDate(String beforeVerifiedDate) {
        if (!StringUtils.isEmpty(beforeVerifiedDate)) {
            this.beforeVerifiedDate = LocalDate.parse(beforeVerifiedDate, DateTimeFormatter.ISO_DATE);
        }
    }

    public void setAfterVerifiedDate(String afterVerifiedDate) {
        if (!StringUtils.isEmpty(afterVerifiedDate)) {
            this.afterVerifiedDate = LocalDate.parse(afterVerifiedDate, DateTimeFormatter.ISO_DATE);
        }
    }
}
