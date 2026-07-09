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
public class IncomeResponse {

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate incomeDate;
    private String source;
    private CategoryResponse category;
}
