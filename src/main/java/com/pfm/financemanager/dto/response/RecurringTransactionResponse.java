package com.pfm.financemanager.dto.response;

import com.pfm.financemanager.entity.RecurrenceFrequency;
import com.pfm.financemanager.entity.TransactionType;
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
public class RecurringTransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private RecurrenceFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextRunDate;
    private boolean active;
    private CategoryResponse category;
}
