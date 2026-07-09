package com.pfm.financemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private List<CategoryBreakdown> expenseByCategory;
    private List<IncomeResponse> recentIncomes;
    private List<ExpenseResponse> recentExpenses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private String categoryName;
        private BigDecimal totalAmount;
        private double percentage;
    }
}
