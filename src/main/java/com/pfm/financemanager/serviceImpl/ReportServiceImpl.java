package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.response.*;
import com.pfm.financemanager.entity.Expense;
import com.pfm.financemanager.entity.Income;
import com.pfm.financemanager.repository.ExpenseRepository;
import com.pfm.financemanager.repository.IncomeRepository;
import com.pfm.financemanager.service.ReportService;
import com.pfm.financemanager.util.DateUtil;
import com.pfm.financemanager.util.ExcelExportUtil;
import com.pfm.financemanager.util.PdfExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final PdfExportUtil pdfExportUtil;
    private final ExcelExportUtil excelExportUtil;

    @Override
    public MonthlyReportResponse getMonthlyReport(Long userId, int month, int year) {
        LocalDate start = DateUtil.firstDayOfMonth(year, month);
        LocalDate end = DateUtil.lastDayOfMonth(year, month);

        List<Income> incomes = incomeRepository.findByUserIdAndIncomeDateBetween(userId, start, end);
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, start, end);

        BigDecimal totalIncome = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return MonthlyReportResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(totalIncome.subtract(totalExpense))
                .incomes(incomes.stream().map(this::mapIncome).collect(Collectors.toList()))
                .expenses(expenses.stream().map(this::mapExpense).collect(Collectors.toList()))
                .build();
    }

    @Override
    public YearlyReportResponse getYearlyReport(Long userId, int year) {
        List<YearlyReportResponse.MonthlySummary> summaries = new ArrayList<>();
        BigDecimal yearIncome = BigDecimal.ZERO;
        BigDecimal yearExpense = BigDecimal.ZERO;

        for (int month = 1; month <= 12; month++) {
            LocalDate start = DateUtil.firstDayOfMonth(year, month);
            LocalDate end = DateUtil.lastDayOfMonth(year, month);

            BigDecimal income = incomeRepository.sumAmountByUserIdAndDateRange(userId, start, end);
            BigDecimal expense = expenseRepository.sumAmountByUserIdAndDateRange(userId, start, end);

            yearIncome = yearIncome.add(income);
            yearExpense = yearExpense.add(expense);

            summaries.add(YearlyReportResponse.MonthlySummary.builder()
                    .month(month)
                    .totalIncome(income)
                    .totalExpense(expense)
                    .netSavings(income.subtract(expense))
                    .build());
        }

        return YearlyReportResponse.builder()
                .year(year)
                .totalIncome(yearIncome)
                .totalExpense(yearExpense)
                .netSavings(yearIncome.subtract(yearExpense))
                .monthlySummaries(summaries)
                .build();
    }

    @Override
    public byte[] exportMonthlyReportPdf(Long userId, int month, int year) {
        MonthlyReportResponse report = getMonthlyReport(userId, month, year);
        return pdfExportUtil.generateMonthlyReportPdf(report);
    }

    @Override
    public byte[] exportMonthlyReportExcel(Long userId, int month, int year) {
        MonthlyReportResponse report = getMonthlyReport(userId, month, year);
        return excelExportUtil.generateMonthlyReportExcel(report);
    }

    private IncomeResponse mapIncome(Income income) {
        CategoryResponse category = income.getCategory() == null ? null : CategoryResponse.builder()
                .id(income.getCategory().getId())
                .name(income.getCategory().getName())
                .type(income.getCategory().getType())
                .systemDefined(income.getCategory().isSystemDefined())
                .build();

        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .description(income.getDescription())
                .incomeDate(income.getIncomeDate())
                .source(income.getSource())
                .category(category)
                .build();
    }

    private ExpenseResponse mapExpense(Expense expense) {
        CategoryResponse category = expense.getCategory() == null ? null : CategoryResponse.builder()
                .id(expense.getCategory().getId())
                .name(expense.getCategory().getName())
                .type(expense.getCategory().getType())
                .systemDefined(expense.getCategory().isSystemDefined())
                .build();

        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .paymentMode(expense.getPaymentMode())
                .receiptUrl(expense.getReceiptUrl())
                .category(category)
                .build();
    }
}
