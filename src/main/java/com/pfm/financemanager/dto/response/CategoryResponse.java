package com.pfm.financemanager.dto.response;

import com.pfm.financemanager.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private TransactionType type;
    private boolean systemDefined;
}
