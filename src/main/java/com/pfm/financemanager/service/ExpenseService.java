package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.ExpenseRequest;
import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface ExpenseService {

    ExpenseResponse createExpense(Long userId, ExpenseRequest request);

    ExpenseResponse getExpenseById(Long userId, Long expenseId);

    ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request);

    void deleteExpense(Long userId, Long expenseId);

    ExpenseResponse uploadReceipt(Long userId, Long expenseId, MultipartFile file);

    PageResponse<ExpenseResponse> getExpenses(Long userId, LocalDate startDate, LocalDate endDate,
                                               Long categoryId, String keyword, int page, int size,
                                               String sortBy, String direction);
}
