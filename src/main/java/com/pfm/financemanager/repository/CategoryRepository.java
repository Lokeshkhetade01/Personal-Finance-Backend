package com.pfm.financemanager.repository;

import com.pfm.financemanager.entity.Category;
import com.pfm.financemanager.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserIdOrSystemDefinedTrue(Long userId);

    List<Category> findByUserIdAndType(Long userId, TransactionType type);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameAndUserId(String name, Long userId);
}
