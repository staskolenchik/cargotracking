package com.itechart.cargotrucking.core.report.sysadmin.service;

import com.itechart.cargotrucking.core.client.ClientStatusEnum;
import com.itechart.cargotrucking.core.client.ClientStatusHistory;
import com.itechart.cargotrucking.core.client.repository.ClientStatusRepository;
import com.itechart.cargotrucking.core.report.exception.InvalidDateIntervalException;
import com.itechart.cargotrucking.core.report.sysadmin.SysAdminReport;
import com.itechart.cargotrucking.core.report.sysadmin.dto.IntervalStatistics;
import com.itechart.cargotrucking.core.report.sysadmin.dto.SysAdminReportCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysAdminReportServiceImpl implements SysAdminReportService {
    @Value("${statistics.consumption.sysadmin}")
    private int sysAdminSalary;

    @Value("${statistics.consumption.dbadmin}")
    private int dbAdminSalary;

    @Value("${statistics.consumption.server-payment}")
    private int serverPayment;

    @Value("${statistics.income}")
    private int incomePerCompany;

    private ClientStatusRepository clientStatusRepository;

    @Autowired
    public SysAdminReportServiceImpl(ClientStatusRepository clientStatusRepository) {
        this.clientStatusRepository = clientStatusRepository;
    }

    @Override
    public SysAdminReport createSysAdminReport(SysAdminReportCreateDto createDto) {
        validateReport(createDto);

        long activeClients = 0;
        long lostClients = 0;
        double income;
        double consumption;

        LocalDateTime start = createDto.getInitialDate().atStartOfDay();
        LocalDateTime end = createDto.getFinalDate().atStartOfDay().plusDays(1);
        int interval = (int) ChronoUnit.DAYS.between(start, end) - 1;

        List<ClientStatusHistory> histories = clientStatusRepository.findAll(
                start, end,
                Sort.by(Sort.Direction.DESC, "date")
        );
        Set<Long> set = new HashSet<>();

        for (ClientStatusHistory clientStatusHistory : histories) {
            if (!set.contains(clientStatusHistory.getClientId())) {
                set.add(clientStatusHistory.getClientId());
                if (clientStatusHistory.getStatus().equals(ClientStatusEnum.ACTIVATED)) {
                    activeClients++;
                } else {
                    lostClients++;
                }
            }
        }

        income = activeClients * interval * incomePerCompany;
        consumption = (dbAdminSalary + sysAdminSalary + serverPayment) * interval;

        IntervalStatistics statistics = new IntervalStatistics(createDto.getInitialDate(), createDto.getFinalDate(), consumption, income, activeClients, lostClients);

        return new SysAdminReport(statistics);
    }

    private void validateReport(SysAdminReportCreateDto createDto) {
        if (createDto.getFinalDate().isBefore(createDto.getInitialDate())) {
            throw new InvalidDateIntervalException();
        }
    }
}
