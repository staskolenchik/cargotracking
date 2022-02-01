package com.itechart.cargotrucking.core.report.sysadmin.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
public class SysAdminReportCreateDto {
    @NotNull(message = "Initial date can't be null")
    private LocalDate initialDate;

    @NotNull(message = "Final date can't be null")
    private LocalDate finalDate;

    public void setInitialDate(String initialDate) {
        if (!StringUtils.isEmpty(initialDate)) {
            this.initialDate = LocalDate.parse(initialDate, DateTimeFormatter.ISO_DATE);
        }
    }

    public void setFinalDate(String finalDate) {
        if (!StringUtils.isEmpty(finalDate)) {
            this.finalDate = LocalDate.parse(finalDate, DateTimeFormatter.ISO_DATE);
        }
    }

    public void setInitialDateFromDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }
}
