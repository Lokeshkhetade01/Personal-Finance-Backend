package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.RecurringTransactionRequest;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.dto.response.RecurringTransactionResponse;
import com.pfm.financemanager.entity.*;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.CategoryRepository;
import com.pfm.financemanager.repository.ExpenseRepository;
import com.pfm.financemanager.repository.IncomeRepository;
import com.pfm.financemanager.repository.RecurringTransactionRepository;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public RecurringTransactionResponse createRecurringTransaction(Long userId, RecurringTransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = resolveCategory(userId, request.getCategoryId());

        RecurringTransaction transaction = RecurringTransaction.builder()
                .user(user)
                .category(category)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .nextRunDate(request.getStartDate())
                .active(true)
                .build();

        return mapToResponse(recurringTransactionRepository.save(transaction));
    }

    @Override
    public List<RecurringTransactionResponse> getRecurringTransactions(Long userId) {
        return recurringTransactionRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecurringTransactionResponse updateRecurringTransaction(Long userId, Long id, RecurringTransactionRequest request) {
        RecurringTransaction transaction = recurringTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found with id: " + id));

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setFrequency(request.getFrequency());
        transaction.setStartDate(request.getStartDate());
        transaction.setEndDate(request.getEndDate());
        transaction.setCategory(resolveCategory(userId, request.getCategoryId()));

        return mapToResponse(recurringTransactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void deleteRecurringTransaction(Long userId, Long id) {
        RecurringTransaction transaction = recurringTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found with id: " + id));
        recurringTransactionRepository.delete(transaction);
    }

    @Override
    @Transactional
    public void processDueRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions = recurringTransactionRepository.findByActiveTrueAndNextRunDateLessThanEqual(today);

        for (RecurringTransaction transaction : dueTransactions) {
            if (transaction.getEndDate() != null && transaction.getEndDate().isBefore(today)) {
                transaction.setActive(false);
                recurringTransactionRepository.save(transaction);
                continue;
            }

            if (transaction.getType() == TransactionType.INCOME) {
                Income income = Income.builder()
                        .user(transaction.getUser())
                        .category(transaction.getCategory())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription() + " (recurring)")
                        .incomeDate(transaction.getNextRunDate())
                        .source("Recurring")
                        .build();
                incomeRepository.save(income);
            } else {
                Expense expense = Expense.builder()
                        .user(transaction.getUser())
                        .category(transaction.getCategory())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription() + " (recurring)")
                        .expenseDate(transaction.getNextRunDate())
                        .paymentMode("Auto")
                        .build();
                expenseRepository.save(expense);
            }

            transaction.setNextRunDate(computeNextRunDate(transaction.getNextRunDate(), transaction.getFrequency()));
            recurringTransactionRepository.save(transaction);
            log.info("Processed recurring transaction id {} for user {}", transaction.getId(), transaction.getUser().getId());
        }
    }

    private LocalDate computeNextRunDate(LocalDate current, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }

    private Category resolveCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private RecurringTransactionResponse mapToResponse(RecurringTransaction transaction) {
        CategoryResponse categoryResponse = null;
        if (transaction.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(transaction.getCategory().getId())
                    .name(transaction.getCategory().getName())
                    .type(transaction.getCategory().getType())
                    .systemDefined(transaction.getCategory().isSystemDefined())
                    .build();
        }

        return RecurringTransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .frequency(transaction.getFrequency())
                .startDate(transaction.getStartDate())
                .endDate(transaction.getEndDate())
                .nextRunDate(transaction.getNextRunDate())
                .active(transaction.isActive())
                .category(categoryResponse)
                .build();
    }
}
