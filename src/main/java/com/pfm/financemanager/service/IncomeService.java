package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.IncomeRequest;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.dto.response.PageResponse;

import java.time.LocalDate;

public interface IncomeService {

    IncomeResponse createIncome(Long userId, IncomeRequest request);

    IncomeResponse getIncomeById(Long userId, Long incomeId);

    IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request);

    void deleteIncome(Long userId, Long incomeId);

    PageResponse<IncomeResponse> getIncomes(Long userId, LocalDate startDate, LocalDate endDate,
                                             String keyword, int page, int size, String sortBy, String direction);
}
