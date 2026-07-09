package com.pfm.financemanager.util;

import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.dto.response.MonthlyReportResponse;
import com.pfm.financemanager.exception.ReportGenerationException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExcelExportUtil {

    public byte[] generateMonthlyReportExcel(MonthlyReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Sheet summarySheet = workbook.createSheet("Summary");
            Row summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Metric");
            summaryHeader.createCell(1).setCellValue("Amount");
            applyStyle(summaryHeader, headerStyle);

            createRow(summarySheet, 1, "Month", String.valueOf(report.getMonth()));
            createRow(summarySheet, 2, "Year", String.valueOf(report.getYear()));
            createRow(summarySheet, 3, "Total Income", report.getTotalIncome().toString());
            createRow(summarySheet, 4, "Total Expense", report.getTotalExpense().toString());
            createRow(summarySheet, 5, "Net Savings", report.getNetSavings().toString());

            Sheet incomeSheet = workbook.createSheet("Incomes");
            Row incomeHeader = incomeSheet.createRow(0);
            String[] incomeColumns = {"Date", "Amount", "Source", "Description"};
            for (int i = 0; i < incomeColumns.length; i++) {
                incomeHeader.createCell(i).setCellValue(incomeColumns[i]);
            }
            applyStyle(incomeHeader, headerStyle);

            int rowIndex = 1;
            for (IncomeResponse income : report.getIncomes()) {
                Row row = incomeSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(income.getIncomeDate().toString());
                row.createCell(1).setCellValue(income.getAmount().doubleValue());
                row.createCell(2).setCellValue(income.getSource() == null ? "" : income.getSource());
                row.createCell(3).setCellValue(income.getDescription() == null ? "" : income.getDescription());
            }

            Sheet expenseSheet = workbook.createSheet("Expenses");
            Row expenseHeader = expenseSheet.createRow(0);
            String[] expenseColumns = {"Date", "Amount", "Category", "Payment Mode", "Description"};
            for (int i = 0; i < expenseColumns.length; i++) {
                expenseHeader.createCell(i).setCellValue(expenseColumns[i]);
            }
            applyStyle(expenseHeader, headerStyle);

            rowIndex = 1;
            for (ExpenseResponse expense : report.getExpenses()) {
                Row row = expenseSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(expense.getExpenseDate().toString());
                row.createCell(1).setCellValue(expense.getAmount().doubleValue());
                row.createCell(2).setCellValue(expense.getCategory() != null ? expense.getCategory().getName() : "");
                row.createCell(3).setCellValue(expense.getPaymentMode() == null ? "" : expense.getPaymentMode());
                row.createCell(4).setCellValue(expense.getDescription() == null ? "" : expense.getDescription());
            }

            for (int i = 0; i < 5; i++) {
                summarySheet.autoSizeColumn(i);
                incomeSheet.autoSizeColumn(i);
                expenseSheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate Excel report", e);
        }
    }

    private void createRow(Sheet sheet, int index, String label, String value) {
        Row row = sheet.createRow(index);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void applyStyle(Row row, CellStyle style) {
        for (Cell cell : row) {
            cell.setCellStyle(style);
        }
    }
}
