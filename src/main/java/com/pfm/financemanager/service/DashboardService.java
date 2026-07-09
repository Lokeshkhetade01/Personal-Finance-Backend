package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.response.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(Long userId, Integer month, Integer year);
}
