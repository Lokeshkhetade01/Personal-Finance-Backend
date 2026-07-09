package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.BudgetRequest;
import com.pfm.financemanager.dto.response.BudgetResponse;

import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(Long userId, BudgetRequest request);

    BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request);

    void deleteBudget(Long userId, Long budgetId);

    List<BudgetResponse> getBudgets(Long userId, Integer month, Integer year);

    List<BudgetResponse> getBudgetAlerts(Long userId, Integer month, Integer year);
}
