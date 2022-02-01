package com.itechart.cargotrucking.core.report.client;

import com.itechart.cargotrucking.core.report.client.dto.DriverStatistic;
import com.itechart.cargotrucking.core.report.client.dto.WeekCompanyStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientReport {
    private List<WeekCompanyStatistic> statistic;
    private List<DriverStatistic> bestFiveDrivers;
}
