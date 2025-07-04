package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

        assertEquals(child.getId(), dto.getId());
        assertEquals(child.getLabel(), dto.getLabel());
        assertEquals(child.getTransactionType(), dto.getTransactionType());
        assertEquals(child.getSmoothType(), dto.getSmoothType());
        assertEquals(child.getIcon(), dto.getIcon());
        assertEquals(parent.getId(), dto.getParentId());
        assertNotNull(dto.getTargets());
        assertEquals(1, dto.getTargets().size());
        TargetDTO t = dto.getTargets().getFirst();
        assertEquals(target.getId(), t.getId());
        assertEquals(child.getId(), t.getCategoryId());
        assertEquals(target.getAmount(), t.getAmount());
    }

    @Test
    void testToDtoNoParent() {
        Category child = new Category();
        child.setId(2L);
        child.setTargets(List.of());

        CategoryMapperImpl mapper = new CategoryMapperImpl();
        CategoryDTO dto = mapper.toDto(child);
        assertNull(dto.getParentId());
        assertTrue(dto.getTargets().isEmpty());
    }

    @Test
    void testNullValues() {
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null, mock(CategoryRepository.class)));
    }

    @Test
    void testNullTargetLists() {
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        Category c = new Category();
        c.setId(1L);
        c.setTargets(null);
        CategoryDTO dto = mapper.toDto(c);
        assertNull(dto.getTargets());

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setTargets(null);
        Category entity = mapper.toEntity(dto2, mock(CategoryRepository.class));
        assertNull(entity.getTargets());
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

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getLabel(), entity.getLabel());
        assertEquals(dto.getTransactionType(), entity.getTransactionType());
        assertEquals(dto.getSmoothType(), entity.getSmoothType());
        assertEquals(dto.getIcon(), entity.getIcon());
        assertSame(parent, entity.getParent());
        assertNotNull(entity.getTargets());
        assertEquals(1, entity.getTargets().size());
        Target mappedTarget = entity.getTargets().getFirst();
        assertEquals(targetDTO.getId(), mappedTarget.getId());
        assertEquals(targetDTO.getAmount(), mappedTarget.getAmount());
        assertSame(childFromRepo, mappedTarget.getCategory());
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
        assertNull(entity.getParent());
        verify(repo).findById(1L);
    }

    @Test
    void testToEntityNullFields() {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();

        CategoryDTO dto = new CategoryDTO();
        dto.setTargets(List.of());

        Category entity = mapper.toEntity(dto, repo);
        assertNull(entity.getParent());
        assertNotNull(entity.getTargets());
        assertTrue(entity.getTargets().isEmpty());
        verifyNoInteractions(repo);
    }

    @Test
    void testMappingHelpers() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();

        Category parent = new Category();
        parent.setId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(parent));

        Method toParent =
                CategoryMapper.class.getDeclaredMethod("mapParentIdToParent", Long.class, CategoryRepository.class);
        toParent.setAccessible(true);
        assertSame(parent, toParent.invoke(mapper, 5L, repo));
        assertNull(toParent.invoke(mapper, null, repo));
        Method toId = CategoryMapper.class.getDeclaredMethod("mapParentToParentId", Category.class);
        toId.setAccessible(true);
        assertEquals(5L, toId.invoke(mapper, parent));
        assertNull(toId.invoke(mapper, (Object) null));
    }
}
