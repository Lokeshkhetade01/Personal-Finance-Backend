package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.CategoryRequest;
import com.pfm.financemanager.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(Long userId, CategoryRequest request);

    List<CategoryResponse> getAllCategories(Long userId);

    CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request);

    void deleteCategory(Long userId, Long categoryId);
}
