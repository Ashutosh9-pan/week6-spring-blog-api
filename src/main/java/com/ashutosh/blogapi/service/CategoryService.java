package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.exception.DuplicateResourceException;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private static final Logger log =
            LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {

        log.info("Creating category with name: {}", category.getName());

        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            log.warn("Category creation failed. Duplicate category name: {}",
                    category.getName());

            throw new DuplicateResourceException(
                    "Category already exists with name: " + category.getName()
            );
        }

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id: {} and name: {}",
                savedCategory.getId(),
                savedCategory.getName());

        return savedCategory;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {

        log.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();

        log.debug("Fetched {} categories", categories.size());

        return categories;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {

        log.debug("Fetching category with id: {}", id);

        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Category not found with id: " + id
                    );
                });
    }

    public Category updateCategory(Long id, Category categoryDetails) {

        log.info("Updating category with id: {}", id);

        Category category = getCategoryById(id);

        categoryRepository.findByNameIgnoreCase(categoryDetails.getName())
                .filter(existingCategory ->
                        !existingCategory.getId().equals(id))
                .ifPresent(existingCategory -> {

                    log.warn(
                            "Category update failed. Duplicate category name: {}",
                            categoryDetails.getName()
                    );

                    throw new DuplicateResourceException(
                            "Category already exists with name: "
                                    + categoryDetails.getName()
                    );
                });

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", id);

        return updatedCategory;
    }

    public void deleteCategory(Long id) {

        log.info("Deleting category with id: {}", id);

        Category category = getCategoryById(id);

        categoryRepository.delete(category);

        log.info("Category deleted successfully with id: {}", id);
    }
}