package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.CategoryRequest;
import com.pfm.financemanager.dto.response.CategoryResponse;
import com.pfm.financemanager.entity.Category;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.DuplicateResourceException;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.CategoryRepository;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .user(user)
                .systemDefined(false)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getAllCategories(Long userId) {
        return categoryRepository.findByUserIdOrSystemDefinedTrue(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        category.setName(request.getName());
        category.setType(request.getType());

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .systemDefined(category.isSystemDefined())
                .build();
    }
}
