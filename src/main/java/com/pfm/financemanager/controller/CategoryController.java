package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.CategoryRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResponse> response = categoryService.getAllCategories(SecurityUtil.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", response));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long categoryId,
                                                                 @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(SecurityUtil.getCurrentUserId(), categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long categoryId) {
        categoryService.deleteCategory(SecurityUtil.getCurrentUserId(), categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}
