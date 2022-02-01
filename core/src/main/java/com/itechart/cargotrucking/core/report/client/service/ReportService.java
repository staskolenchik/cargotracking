package com.itechart.cargotrucking.core.report.client.service;

import com.itechart.cargotrucking.core.report.client.ClientReport;
import com.itechart.cargotrucking.core.report.client.dto.ClientReportCreateDto;

public interface ReportService {
    ClientReport createClientReport(long clientId, ClientReportCreateDto createDto);
}
