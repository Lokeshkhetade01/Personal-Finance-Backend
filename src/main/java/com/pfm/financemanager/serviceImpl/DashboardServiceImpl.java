package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.response.DashboardResponse;
import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.entity.Expense;
import com.pfm.financemanager.entity.Income;
import com.pfm.financemanager.repository.ExpenseRepository;
import com.pfm.financemanager.repository.IncomeRepository;
import com.pfm.financemanager.service.DashboardService;
import com.pfm.financemanager.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public DashboardResponse getDashboard(Long userId, Integer month, Integer year) {
        int resolvedMonth = month != null ? month : LocalDate.now().getMonthValue();
        int resolvedYear = year != null ? year : LocalDate.now().getYear();

        LocalDate start = DateUtil.firstDayOfMonth(resolvedYear, resolvedMonth);
        LocalDate end = DateUtil.lastDayOfMonth(resolvedYear, resolvedMonth);

        BigDecimal totalIncome = incomeRepository.sumAmountByUserIdAndDateRange(userId, start, end);
        BigDecimal totalExpense = expenseRepository.sumAmountByUserIdAndDateRange(userId, start, end);
        BigDecimal savings = totalIncome.subtract(totalExpense);

        List<Object[]> grouped = expenseRepository.sumAmountGroupedByCategory(userId, start, end);
        List<DashboardResponse.CategoryBreakdown> breakdown = grouped.stream()
                .map(row -> {
                    String name = row[0] == null ? "Uncategorized" : row[0].toString();
                    BigDecimal amount = (BigDecimal) row[1];
                    double percentage = totalExpense.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                            : amount.divide(totalExpense, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                    return DashboardResponse.CategoryBreakdown.builder()
                            .categoryName(name)
                            .totalAmount(amount)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        List<Income> recentIncomes = incomeRepository.findByUserId(userId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "incomeDate"))).getContent();
        List<Expense> recentExpenses = expenseRepository.findByUserId(userId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "expenseDate"))).getContent();

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalSavings(savings)
                .expenseByCategory(breakdown)
                .recentIncomes(recentIncomes.stream().map(this::mapIncome).collect(Collectors.toList()))
                .recentExpenses(recentExpenses.stream().map(this::mapExpense).collect(Collectors.toList()))
                .build();
    }

    private IncomeResponse mapIncome(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .description(income.getDescription())
                .incomeDate(income.getIncomeDate())
                .source(income.getSource())
                .build();
    }

    private ExpenseResponse mapExpense(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .paymentMode(expense.getPaymentMode())
                .receiptUrl(expense.getReceiptUrl())
                .build();
    }
}
