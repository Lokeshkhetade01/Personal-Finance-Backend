package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.response.MonthlyReportResponse;
import com.pfm.financemanager.dto.response.YearlyReportResponse;

public interface ReportService {

    MonthlyReportResponse getMonthlyReport(Long userId, int month, int year);

    YearlyReportResponse getYearlyReport(Long userId, int year);

    byte[] exportMonthlyReportPdf(Long userId, int month, int year);

    byte[] exportMonthlyReportExcel(Long userId, int month, int year);
}
