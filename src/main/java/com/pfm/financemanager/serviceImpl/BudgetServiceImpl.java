package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.BudgetRequest;
import com.pfm.financemanager.dto.response.BudgetResponse;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.entity.Budget;
import com.pfm.financemanager.entity.Category;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.DuplicateResourceException;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.BudgetRepository;
import com.pfm.financemanager.repository.CategoryRepository;
import com.pfm.financemanager.repository.ExpenseRepository;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.BudgetService;
import com.pfm.financemanager.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = resolveCategory(userId, request.getCategoryId());

        budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(userId, request.getCategoryId(), request.getMonth(), request.getYear())
                .ifPresent(b -> {
                    throw new DuplicateResourceException("Budget already exists for this category and period");
                });

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .limitAmount(request.getLimitAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        return mapToResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        budget.setCategory(resolveCategory(userId, request.getCategoryId()));
        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        return mapToResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        budgetRepository.delete(budget);
    }

    @Override
    public List<BudgetResponse> getBudgets(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponse> getBudgetAlerts(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::mapToResponse)
                .filter(BudgetResponse::isLimitExceeded)
                .collect(Collectors.toList());
    }

    private Category resolveCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private BudgetResponse mapToResponse(Budget budget) {
        BigDecimal spent = BigDecimal.ZERO;
        LocalDate start = DateUtil.firstDayOfMonth(budget.getYear(), budget.getMonth());
        LocalDate end = DateUtil.lastDayOfMonth(budget.getYear(), budget.getMonth());

        CategoryResponse categoryResponse = null;
        if (budget.getCategory() != null) {
            spent = expenseRepository.sumAmountByUserIdAndCategoryAndDateRange(
                    budget.getUser().getId(), budget.getCategory().getId(), start, end);
            categoryResponse = CategoryResponse.builder()
                    .id(budget.getCategory().getId())
                    .name(budget.getCategory().getName())
                    .type(budget.getCategory().getType())
                    .systemDefined(budget.getCategory().isSystemDefined())
                    .build();
        } else {
            spent = expenseRepository.sumAmountByUserIdAndDateRange(budget.getUser().getId(), start, end);
        }

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double utilization = budget.getLimitAmount().compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();

        return BudgetResponse.builder()
                .id(budget.getId())
                .category(categoryResponse)
                .limitAmount(budget.getLimitAmount())
                .spentAmount(spent)
                .remainingAmount(remaining)
                .utilizationPercentage(utilization)
                .limitExceeded(spent.compareTo(budget.getLimitAmount()) > 0)
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }
}
