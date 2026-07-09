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
public class YearlyReportResponse {

    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private List<MonthlySummary> monthlySummaries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySummary {
        private int month;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netSavings;
    }
}
