package com.pfm.financemanager.repository;

import com.pfm.financemanager.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByUserId(Long userId);

    Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);

    List<RecurringTransaction> findByActiveTrueAndNextRunDateLessThanEqual(LocalDate date);
}
