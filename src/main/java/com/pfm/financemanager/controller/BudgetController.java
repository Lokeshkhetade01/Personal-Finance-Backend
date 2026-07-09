package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.BudgetRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.BudgetResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> create(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Budget created successfully", response));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(@PathVariable Long budgetId,
                                                               @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(SecurityUtil.getCurrentUserId(), budgetId, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long budgetId) {
        budgetService.deleteBudget(SecurityUtil.getCurrentUserId(), budgetId);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAll(@RequestParam Integer month, @RequestParam Integer year) {
        List<BudgetResponse> response = budgetService.getBudgets(SecurityUtil.getCurrentUserId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Budgets fetched successfully", response));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAlerts(@RequestParam Integer month, @RequestParam Integer year) {
        List<BudgetResponse> response = budgetService.getBudgetAlerts(SecurityUtil.getCurrentUserId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Budget alerts fetched successfully", response));
    }
}
