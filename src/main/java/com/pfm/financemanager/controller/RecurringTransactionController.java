package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.RecurringTransactionRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.RecurringTransactionResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.RecurringTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> create(@Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransactionResponse response = recurringTransactionService.createRecurringTransaction(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Recurring transaction created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringTransactionResponse>>> getAll() {
        List<RecurringTransactionResponse> response = recurringTransactionService.getRecurringTransactions(SecurityUtil.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Recurring transactions fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> update(@PathVariable Long id,
                                                                             @Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransactionResponse response = recurringTransactionService.updateRecurringTransaction(SecurityUtil.getCurrentUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recurringTransactionService.deleteRecurringTransaction(SecurityUtil.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction deleted successfully"));
    }
}
