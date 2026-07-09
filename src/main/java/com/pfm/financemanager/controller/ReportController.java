package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.MonthlyReportResponse;
import com.pfm.financemanager.dto.response.YearlyReportResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(@RequestParam int month, @RequestParam int year) {
        MonthlyReportResponse response = reportService.getMonthlyReport(SecurityUtil.getCurrentUserId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Monthly report fetched successfully", response));
    }

    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse<YearlyReportResponse>> getYearlyReport(@RequestParam int year) {
        YearlyReportResponse response = reportService.getYearlyReport(SecurityUtil.getCurrentUserId(), year);
        return ResponseEntity.ok(ApiResponse.success("Yearly report fetched successfully", response));
    }

    @GetMapping("/monthly/export/pdf")
    public ResponseEntity<byte[]> exportMonthlyReportPdf(@RequestParam int month, @RequestParam int year) {
        byte[] pdfBytes = reportService.exportMonthlyReportPdf(SecurityUtil.getCurrentUserId(), month, year);
        String fileName = "monthly-report-" + month + "-" + year + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/monthly/export/excel")
    public ResponseEntity<byte[]> exportMonthlyReportExcel(@RequestParam int month, @RequestParam int year) {
        byte[] excelBytes = reportService.exportMonthlyReportExcel(SecurityUtil.getCurrentUserId(), month, year);
        String fileName = "monthly-report-" + month + "-" + year + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
