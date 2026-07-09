package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.IncomeRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.dto.response.PageResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> create(@Valid @RequestBody IncomeRequest request) {
        IncomeResponse response = incomeService.createIncome(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Income created successfully", response));
    }

    @GetMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<IncomeResponse>> getById(@PathVariable Long incomeId) {
        IncomeResponse response = incomeService.getIncomeById(SecurityUtil.getCurrentUserId(), incomeId);
        return ResponseEntity.ok(ApiResponse.success("Income fetched successfully", response));
    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<IncomeResponse>> update(@PathVariable Long incomeId,
                                                               @Valid @RequestBody IncomeRequest request) {
        IncomeResponse response = incomeService.updateIncome(SecurityUtil.getCurrentUserId(), incomeId, request);
        return ResponseEntity.ok(ApiResponse.success("Income updated successfully", response));
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long incomeId) {
        incomeService.deleteIncome(SecurityUtil.getCurrentUserId(), incomeId);
        return ResponseEntity.ok(ApiResponse.success("Income deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IncomeResponse>>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "incomeDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        PageResponse<IncomeResponse> response = incomeService.getIncomes(
                SecurityUtil.getCurrentUserId(), startDate, endDate, keyword, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Incomes fetched successfully", response));
    }
}
