package com.pfm.financemanager.util;

import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.dto.response.MonthlyReportResponse;
import com.pfm.financemanager.exception.ReportGenerationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfExportUtil {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 18;

    public byte[] generateMonthlyReportPdf(MonthlyReportResponse report) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = page.getMediaBox().getHeight() - MARGIN;

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font normalFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            content.beginText();
            content.setFont(titleFont, 16);
            content.newLineAtOffset(MARGIN, y);
            content.showText("Monthly Financial Report - " + report.getMonth() + "/" + report.getYear());
            content.endText();
            y -= LINE_HEIGHT * 2;

            content = writeLine(content, page, document, normalFont, "Total Income: " + report.getTotalIncome(), y);
            y -= LINE_HEIGHT;
            content = writeLine(content, page, document, normalFont, "Total Expense: " + report.getTotalExpense(), y);
            y -= LINE_HEIGHT;
            content = writeLine(content, page, document, normalFont, "Net Savings: " + report.getNetSavings(), y);
            y -= LINE_HEIGHT * 2;

            content.beginText();
            content.setFont(titleFont, 13);
            content.newLineAtOffset(MARGIN, y);
            content.showText("Incomes");
            content.endText();
            y -= LINE_HEIGHT;

            for (IncomeResponse income : report.getIncomes()) {
                if (y < MARGIN) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }
                String line = income.getIncomeDate() + " | " + income.getAmount() + " | " + safe(income.getDescription());
                content.beginText();
                content.setFont(normalFont, 10);
                content.newLineAtOffset(MARGIN, y);
                content.showText(line);
                content.endText();
                y -= LINE_HEIGHT;
            }

            y -= LINE_HEIGHT;
            content.beginText();
            content.setFont(titleFont, 13);
            content.newLineAtOffset(MARGIN, y);
            content.showText("Expenses");
            content.endText();
            y -= LINE_HEIGHT;

            for (ExpenseResponse expense : report.getExpenses()) {
                if (y < MARGIN) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }
                String line = expense.getExpenseDate() + " | " + expense.getAmount() + " | " + safe(expense.getDescription());
                content.beginText();
                content.setFont(normalFont, 10);
                content.newLineAtOffset(MARGIN, y);
                content.showText(line);
                content.endText();
                y -= LINE_HEIGHT;
            }

            content.close();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate PDF report", e);
        }
    }

    private PDPageContentStream writeLine(PDPageContentStream content, PDPage page, PDDocument document,
                                           PDType1Font font, String text, float y) throws IOException {
        content.beginText();
        content.setFont(font, 11);
        content.newLineAtOffset(MARGIN, y);
        content.showText(text);
        content.endText();
        return content;
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
