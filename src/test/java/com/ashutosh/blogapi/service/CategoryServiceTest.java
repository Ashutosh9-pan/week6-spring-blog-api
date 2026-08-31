package com.ashutosh.blogapi.service;

import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.exception.DuplicateResourceException;
import com.ashutosh.blogapi.exception.ResourceNotFoundException;
import com.ashutosh.blogapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(
                1L,
                "Technology",
                "Technology and programming articles"
        );
    }

    @Test
    void shouldCreateCategorySuccessfully() {

        when(categoryRepository.existsByNameIgnoreCase("Technology"))
                .thenReturn(false);

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category savedCategory = categoryService.createCategory(category);

        assertNotNull(savedCategory);
        assertEquals(1L, savedCategory.getId());
        assertEquals("Technology", savedCategory.getName());

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void shouldThrowExceptionWhenCategoryAlreadyExists() {

        when(categoryRepository.existsByNameIgnoreCase("Technology"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.createCategory(category)
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void shouldGetCategoryByIdSuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Technology", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(999L)
        );
    }

    @Test
    void shouldDeleteCategorySuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1))
                .delete(category);
    }
}