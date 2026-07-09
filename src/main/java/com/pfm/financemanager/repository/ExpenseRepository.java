package com.pfm.financemanager.repository;

import com.pfm.financemanager.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    Page<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate start, LocalDate end, Pageable pageable);

    Page<Expense> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    Page<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    Page<Expense> findByUserId(Long userId, Pageable pageable);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUserIdAndCategoryIdAndExpenseDateBetween(Long userId, Long categoryId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumAmountByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumAmountByUserIdAndCategoryAndDateRange(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.category.name, COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end GROUP BY e.category.name")
    List<Object[]> sumAmountGroupedByCategory(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
