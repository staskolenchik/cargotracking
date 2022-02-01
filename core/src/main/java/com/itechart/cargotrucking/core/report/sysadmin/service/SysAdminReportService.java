package com.itechart.cargotrucking.core.report.sysadmin.service;

import com.itechart.cargotrucking.core.report.sysadmin.SysAdminReport;
import com.itechart.cargotrucking.core.report.sysadmin.dto.SysAdminReportCreateDto;

public interface SysAdminReportService {
    SysAdminReport createSysAdminReport(SysAdminReportCreateDto createDto);
}
