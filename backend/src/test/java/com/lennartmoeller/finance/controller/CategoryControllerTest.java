package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.service.CategoryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CategoryControllerTest {

    private CategoryService service;
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        service = mock(CategoryService.class);
        controller = new CategoryController(service);
    }

    @Test
    void testGetCategories() {
        List<CategoryDTO> list = List.of(new CategoryDTO(), new CategoryDTO());
        when(service.findAll()).thenReturn(list);

        List<CategoryDTO> result = controller.getCategories();

        assertEquals(list, result);
        verify(service).findAll();
    }

    @Test
    void testGetCategoryByIdFound() {
        CategoryDTO dto = new CategoryDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateOrUpdateCategoryExisting() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(5L);
        CategoryDTO saved = new CategoryDTO();

        when(service.findById(5L)).thenReturn(Optional.of(new CategoryDTO()));
        when(service.save(dto)).thenReturn(saved);

        CategoryDTO result = controller.createOrUpdateCategory(dto);

        assertEquals(saved, result);
        assertEquals(5L, dto.getId());
        verify(service).save(dto);
    }

    @Test
    void testCreateOrUpdateCategoryNew() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(5L);
        CategoryDTO saved = new CategoryDTO();

        when(service.findById(5L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        CategoryDTO result = controller.createOrUpdateCategory(dto);

        assertEquals(saved, result);
        ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
        verify(service).save(captor.capture());
        assertNull(captor.getValue().getId());
    }

    @Test
    void testDeleteCategory() {
        ResponseEntity<Void> response = controller.deleteCategory(9L);

        verify(service).deleteById(9L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
