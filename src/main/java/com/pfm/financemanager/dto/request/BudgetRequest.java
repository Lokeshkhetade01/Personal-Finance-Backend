package com.pfm.financemanager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    private Long categoryId;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "0.01", message = "Limit must be greater than zero")
    private BigDecimal limitAmount;

    @NotNull(message = "Month is required")
    @Min(1) @Max(12)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(2000) @Max(2100)
    private Integer year;
}
