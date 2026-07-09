package com.pfm.financemanager.repository;

import com.pfm.financemanager.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);

    List<Budget> findByUserId(Long userId);
}
