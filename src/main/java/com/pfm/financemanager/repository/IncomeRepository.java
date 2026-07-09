package com.pfm.financemanager.repository;

import com.pfm.financemanager.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<Income> findByIdAndUserId(Long id, Long userId);

    Page<Income> findByUserIdAndIncomeDateBetween(Long userId, LocalDate start, LocalDate end, Pageable pageable);

    Page<Income> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    Page<Income> findByUserId(Long userId, Pageable pageable);

    List<Income> findByUserIdAndIncomeDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId AND i.incomeDate BETWEEN :start AND :end")
    BigDecimal sumAmountByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
