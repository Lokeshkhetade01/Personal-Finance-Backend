package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.DashboardResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        DashboardResponse response = dashboardService.getDashboard(SecurityUtil.getCurrentUserId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched successfully", response));
    }
}
