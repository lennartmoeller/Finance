package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.service.CategoryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryController controller;

    @Test
    void shouldReturnAllCategories() {
        List<CategoryDTO> list = List.of(new CategoryDTO(), new CategoryDTO());
        when(service.findAll()).thenReturn(list);

        List<CategoryDTO> result = controller.getCategories();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldReturnCategoryWhenIdExists() {
        CategoryDTO dto = new CategoryDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundForUnknownId() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldUpdateExistingCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(5L);
        CategoryDTO saved = new CategoryDTO();
        when(service.findById(5L)).thenReturn(Optional.of(new CategoryDTO()));
        when(service.save(dto)).thenReturn(saved);

        CategoryDTO result = controller.createOrUpdateCategory(dto);

        assertThat(result).isSameAs(saved);
        assertThat(dto.getId()).isEqualTo(5L);
        verify(service).save(dto);
    }

    @Test
    void shouldCreateNewCategoryWhenIdUnknown() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(5L);
        CategoryDTO saved = new CategoryDTO();
        when(service.findById(5L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        CategoryDTO result = controller.createOrUpdateCategory(dto);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
        verify(service).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void shouldCreateCategoryWhenIdIsNull() {
        CategoryDTO dto = new CategoryDTO();
        when(service.save(dto)).thenReturn(dto);

        CategoryDTO result = controller.createOrUpdateCategory(dto);

        assertThat(result).isSameAs(dto);
        verify(service).save(dto);
        verify(service, never()).findById(any());
    }

    @Test
    void shouldDeleteCategory() {
        ResponseEntity<Void> response = controller.deleteCategory(9L);

        verify(service).deleteById(9L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
