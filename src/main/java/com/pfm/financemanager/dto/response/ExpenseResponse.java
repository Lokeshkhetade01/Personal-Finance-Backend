package com.pfm.financemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private String paymentMode;
    private String receiptUrl;
    private CategoryResponse category;
    private boolean budgetExceeded;
    private String budgetAlertMessage;
}
