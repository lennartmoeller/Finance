package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {
    private static void inject(Object target, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField("targetMapper");
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void testToDto() throws Exception {
        Category parent = new Category();
        parent.setId(1L);

        Category child = new Category();
        child.setId(2L);
        child.setParent(parent);
        child.setLabel("Food");
        child.setTransactionType(TransactionType.EXPENSE);
        child.setSmoothType(CategorySmoothType.MONTHLY);
        child.setIcon("icon");

        Target target = new Target();
        target.setId(3L);
        target.setCategory(child);
        target.setAmount(50L);
        child.setTargets(List.of(target));

        TargetMapperImpl targetMapper = new TargetMapperImpl();
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, targetMapper);

        CategoryDTO dto = mapper.toDto(child);

        assertThat(dto.getId()).isEqualTo(child.getId());
        assertThat(dto.getLabel()).isEqualTo(child.getLabel());
        assertThat(dto.getTransactionType()).isEqualTo(child.getTransactionType());
        assertThat(dto.getSmoothType()).isEqualTo(child.getSmoothType());
        assertThat(dto.getIcon()).isEqualTo(child.getIcon());
        assertThat(dto.getParentId()).isEqualTo(parent.getId());
        assertThat(dto.getTargets()).hasSize(1);
        TargetDTO t = dto.getTargets().getFirst();
        assertThat(t.getId()).isEqualTo(target.getId());
        assertThat(t.getCategoryId()).isEqualTo(child.getId());
        assertThat(t.getAmount()).isEqualTo(target.getAmount());
    }

    @Test
    void testToDtoNoParent() {
        Category child = new Category();
        child.setId(2L);
        child.setTargets(List.of());

        CategoryMapperImpl mapper = new CategoryMapperImpl();
        CategoryDTO dto = mapper.toDto(child);
        assertThat(dto.getParentId()).isNull();
        assertThat(dto.getTargets()).isEmpty();
    }

    @Test
    void testNullValues() {
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null, mock(CategoryRepository.class))).isNull();
    }

    @Test
    void testNullTargetLists() {
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        Category c = new Category();
        c.setId(1L);
        c.setTargets(null);
        CategoryDTO dto = mapper.toDto(c);
        assertThat(dto.getTargets()).isNull();

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setTargets(null);
        Category entity = mapper.toEntity(dto2, mock(CategoryRepository.class));
        assertThat(entity.getTargets()).isNull();
    }

    @Test
    void testToEntityUsesRepository() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);

        Category parent = new Category();
        parent.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(parent));

        Category childFromRepo = new Category();
        childFromRepo.setId(2L);
        when(repo.findById(2L)).thenReturn(Optional.of(childFromRepo));

        TargetMapperImpl targetMapper = new TargetMapperImpl();
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, targetMapper);

        TargetDTO targetDTO = new TargetDTO();
        targetDTO.setId(5L);
        targetDTO.setCategoryId(2L);
        targetDTO.setAmount(100L);

        CategoryDTO dto = new CategoryDTO();
        dto.setId(2L);
        dto.setParentId(1L);
        dto.setLabel("Food");
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setSmoothType(CategorySmoothType.MONTHLY);
        dto.setIcon("icon");
        dto.setTargets(List.of(targetDTO));

        Category entity = mapper.toEntity(dto, repo);

        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getLabel()).isEqualTo(dto.getLabel());
        assertThat(entity.getTransactionType()).isEqualTo(dto.getTransactionType());
        assertThat(entity.getSmoothType()).isEqualTo(dto.getSmoothType());
        assertThat(entity.getIcon()).isEqualTo(dto.getIcon());
        assertThat(entity.getParent()).isSameAs(parent);
        assertThat(entity.getTargets()).hasSize(1);
        Target mappedTarget = entity.getTargets().getFirst();
        assertThat(mappedTarget.getId()).isEqualTo(targetDTO.getId());
        assertThat(mappedTarget.getAmount()).isEqualTo(targetDTO.getAmount());
        assertThat(mappedTarget.getCategory()).isSameAs(childFromRepo);
        verify(repo).findById(1L);
        verify(repo).findById(2L);
    }

    @Test
    void testToEntityMissingParent() {
        CategoryRepository repo = mock(CategoryRepository.class);
        when(repo.findById(1L)).thenReturn(Optional.empty());
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        CategoryDTO dto = new CategoryDTO();
        dto.setParentId(1L);

        Category entity = mapper.toEntity(dto, repo);
        assertThat(entity.getParent()).isNull();
        verify(repo).findById(1L);
    }

    @Test
    void testToEntityNullFields() {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();

        CategoryDTO dto = new CategoryDTO();
        dto.setTargets(List.of());

        Category entity = mapper.toEntity(dto, repo);
        assertThat(entity.getParent()).isNull();
        assertThat(entity.getTargets()).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void testMappingHelpers() {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();

        Category parent = new Category();
        parent.setId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(parent));

        assertThat(mapper.mapParentIdToParent(5L, repo)).isSameAs(parent);
        assertThat(mapper.mapParentIdToParent(null, repo)).isNull();
        assertThat(mapper.mapParentToParentId(parent)).isEqualTo(5L);
        assertThat(mapper.mapParentToParentId(null)).isNull();
    }
}
