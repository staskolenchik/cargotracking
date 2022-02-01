package com.itechart.cargotrucking.webapp.report;

import com.itechart.cargotrucking.core.report.client.ClientReport;
import com.itechart.cargotrucking.core.report.client.dto.ClientReportCreateDto;
import com.itechart.cargotrucking.core.report.client.service.ReportService;
import com.itechart.cargotrucking.core.report.sysadmin.SysAdminReport;
import com.itechart.cargotrucking.core.report.sysadmin.dto.SysAdminReportCreateDto;
import com.itechart.cargotrucking.core.report.sysadmin.service.SysAdminReportService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportService reportService;
    private SysAdminReportService sysAdminReportService;

    @Autowired
    public ReportController(ReportService reportService, SysAdminReportService sysAdminReportService) {
        this.reportService = reportService;
        this.sysAdminReportService = sysAdminReportService;
    }

    @GetMapping("/client")
    public ClientReport createClientReport(@Valid ClientReportCreateDto createDto, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return reportService.createClientReport(userCredentials.getClientId(), createDto);
    }

    @GetMapping("/sysadmin")
    public SysAdminReport createSysAdminReport(@Valid SysAdminReportCreateDto createDto) {
        return sysAdminReportService.createSysAdminReport(createDto);
    }
}
