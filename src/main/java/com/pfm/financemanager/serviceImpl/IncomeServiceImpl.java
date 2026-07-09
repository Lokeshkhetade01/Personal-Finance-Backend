package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.IncomeRequest;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.dto.response.IncomeResponse;
import com.pfm.financemanager.dto.response.PageResponse;
import com.pfm.financemanager.entity.Category;
import com.pfm.financemanager.entity.Income;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.CategoryRepository;
import com.pfm.financemanager.repository.IncomeRepository;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = resolveCategory(userId, request.getCategoryId());

        Income income = Income.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .incomeDate(request.getIncomeDate())
                .source(request.getSource())
                .build();

        return mapToResponse(incomeRepository.save(income));
    }

    @Override
    public IncomeResponse getIncomeById(Long userId, Long incomeId) {
        return mapToResponse(findIncome(userId, incomeId));
    }

    @Override
    @Transactional
    public IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request) {
        Income income = findIncome(userId, incomeId);

        income.setAmount(request.getAmount());
        income.setDescription(request.getDescription());
        income.setIncomeDate(request.getIncomeDate());
        income.setSource(request.getSource());
        income.setCategory(resolveCategory(userId, request.getCategoryId()));

        return mapToResponse(incomeRepository.save(income));
    }

    @Override
    @Transactional
    public void deleteIncome(Long userId, Long incomeId) {
        Income income = findIncome(userId, incomeId);
        incomeRepository.delete(income);
    }

    @Override
    public PageResponse<IncomeResponse> getIncomes(Long userId, LocalDate startDate, LocalDate endDate,
                                                    String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Income> incomePage;
        if (StringUtils.hasText(keyword)) {
            incomePage = incomeRepository.findByUserIdAndDescriptionContainingIgnoreCase(userId, keyword, pageable);
        } else if (startDate != null && endDate != null) {
            incomePage = incomeRepository.findByUserIdAndIncomeDateBetween(userId, startDate, endDate, pageable);
        } else {
            incomePage = incomeRepository.findByUserId(userId, pageable);
        }

        return PageResponse.from(incomePage.map(this::mapToResponse));
    }

    private Income findIncome(Long userId, Long incomeId) {
        return incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + incomeId));
    }

    private Category resolveCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private IncomeResponse mapToResponse(Income income) {
        CategoryResponse categoryResponse = null;
        if (income.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(income.getCategory().getId())
                    .name(income.getCategory().getName())
                    .type(income.getCategory().getType())
                    .systemDefined(income.getCategory().isSystemDefined())
                    .build();
        }

        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .description(income.getDescription())
                .incomeDate(income.getIncomeDate())
                .source(income.getSource())
                .category(categoryResponse)
                .build();
    }
}
