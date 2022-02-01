package com.itechart.cargotrucking.core.report.sysadmin.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IntervalStatistics {
    LocalDate startIntervalDate;
    LocalDate endIntervalDate;
    double profit;
    double income;
    double consumption;
    long activeClients;
    long lostClients;

    public IntervalStatistics(LocalDate startIntervalDate, LocalDate endIntervalDate, double consumption, double income, long activeClients, long lostClients) {
        this.startIntervalDate = startIntervalDate;
        this.endIntervalDate = endIntervalDate;
        this.consumption = consumption;
        this.income = income;
        profit = income - consumption;
        this.activeClients = activeClients;
        this.lostClients = lostClients;
    }


}
