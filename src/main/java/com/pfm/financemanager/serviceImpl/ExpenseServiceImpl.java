package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.ExpenseRequest;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.dto.response.ExpenseResponse;
import com.pfm.financemanager.dto.response.PageResponse;
import com.pfm.financemanager.entity.Budget;
import com.pfm.financemanager.entity.Category;
import com.pfm.financemanager.entity.Expense;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.BudgetRepository;
import com.pfm.financemanager.repository.CategoryRepository;
import com.pfm.financemanager.repository.ExpenseRepository;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.ExpenseService;
import com.pfm.financemanager.service.FileStorageService;
import com.pfm.financemanager.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ExpenseResponse createExpense(Long userId, ExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = resolveCategory(userId, request.getCategoryId());

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .paymentMode(request.getPaymentMode())
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapToResponseWithBudgetCheck(saved);
    }

    @Override
    public ExpenseResponse getExpenseById(Long userId, Long expenseId) {
        return mapToResponseWithBudgetCheck(findExpense(userId, expenseId));
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = findExpense(userId, expenseId);

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMode(request.getPaymentMode());
        expense.setCategory(resolveCategory(userId, request.getCategoryId()));

        return mapToResponseWithBudgetCheck(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = findExpense(userId, expenseId);
        expenseRepository.delete(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse uploadReceipt(Long userId, Long expenseId, MultipartFile file) {
        Expense expense = findExpense(userId, expenseId);
        String fileUrl = fileStorageService.storeReceiptFile(file, userId);
        expense.setReceiptUrl(fileUrl);
        return mapToResponseWithBudgetCheck(expenseRepository.save(expense));
    }

    @Override
    public PageResponse<ExpenseResponse> getExpenses(Long userId, LocalDate startDate, LocalDate endDate,
                                                      Long categoryId, String keyword, int page, int size,
                                                      String sortBy, String direction) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Expense> expensePage;
        if (StringUtils.hasText(keyword)) {
            expensePage = expenseRepository.findByUserIdAndDescriptionContainingIgnoreCase(userId, keyword, pageable);
        } else if (categoryId != null) {
            expensePage = expenseRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else if (startDate != null && endDate != null) {
            expensePage = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate, pageable);
        } else {
            expensePage = expenseRepository.findByUserId(userId, pageable);
        }

        return PageResponse.from(expensePage.map(this::mapToResponseWithBudgetCheck));
    }

    private Expense findExpense(Long userId, Long expenseId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    private Category resolveCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private ExpenseResponse mapToResponseWithBudgetCheck(Expense expense) {
        CategoryResponse categoryResponse = null;
        boolean budgetExceeded = false;
        String alertMessage = null;

        if (expense.getCategory() != null) {
            Category category = expense.getCategory();
            categoryResponse = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .type(category.getType())
                    .systemDefined(category.isSystemDefined())
                    .build();

            int month = expense.getExpenseDate().getMonthValue();
            int year = expense.getExpenseDate().getYear();

            Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    expense.getUser().getId(), category.getId(), month, year);

            if (budgetOpt.isPresent()) {
                Budget budget = budgetOpt.get();
                LocalDate start = DateUtil.firstDayOfMonth(year, month);
                LocalDate end = DateUtil.lastDayOfMonth(year, month);
                BigDecimal spent = expenseRepository.sumAmountByUserIdAndCategoryAndDateRange(
                        expense.getUser().getId(), category.getId(), start, end);

                if (spent.compareTo(budget.getLimitAmount()) > 0) {
                    budgetExceeded = true;
                    alertMessage = "Budget limit exceeded for category '" + category.getName()
                            + "'. Limit: " + budget.getLimitAmount() + ", Spent: " + spent;
                }
            }
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .paymentMode(expense.getPaymentMode())
                .receiptUrl(expense.getReceiptUrl())
                .category(categoryResponse)
                .budgetExceeded(budgetExceeded)
                .budgetAlertMessage(alertMessage)
                .build();
    }
}
