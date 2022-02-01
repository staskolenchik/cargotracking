package com.itechart.cargotrucking.core.report.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WeekCompanyStatistic {
    LocalDate startWeekDate;
    LocalDate endWeekDate;
    double consumption;
    double income;
    double profit;

    public WeekCompanyStatistic(LocalDate startWeekDate, LocalDate endWeekDate, double consumption, double income) {
        this.startWeekDate = startWeekDate;
        this.endWeekDate = endWeekDate;
        this.consumption = consumption;
        this.income = income;
        profit = income - consumption;
    }
}
