package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.RecurringTransactionRequest;
import com.pfm.financemanager.dto.response.RecurringTransactionResponse;

import java.util.List;

public interface RecurringTransactionService {

    RecurringTransactionResponse createRecurringTransaction(Long userId, RecurringTransactionRequest request);

    List<RecurringTransactionResponse> getRecurringTransactions(Long userId);

    RecurringTransactionResponse updateRecurringTransaction(Long userId, Long id, RecurringTransactionRequest request);

    void deleteRecurringTransaction(Long userId, Long id);

    void processDueRecurringTransactions();
}
