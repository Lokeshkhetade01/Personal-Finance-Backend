package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.ExpenseRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.PageResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense created successfully", response));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long expenseId) {
        ExpenseResponse response = expenseService.getExpenseById(SecurityUtil.getCurrentUserId(), expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", response));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(@PathVariable Long expenseId,
                                                                @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(SecurityUtil.getCurrentUserId(), expenseId, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long expenseId) {
        expenseService.deleteExpense(SecurityUtil.getCurrentUserId(), expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    @PostMapping(value = "/{expenseId}/receipt", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ExpenseResponse>> uploadReceipt(@PathVariable Long expenseId,
                                                                       @RequestParam("file") MultipartFile file) {
        ExpenseResponse response = expenseService.uploadReceipt(SecurityUtil.getCurrentUserId(), expenseId, file);
        return ResponseEntity.ok(ApiResponse.success("Receipt uploaded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ExpenseResponse>>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        PageResponse<ExpenseResponse> response = expenseService.getExpenses(
                SecurityUtil.getCurrentUserId(), startDate, endDate, categoryId, keyword, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", response));
    }
}
