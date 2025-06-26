package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryMapperTest {

    private static void inject(Object target, Class<?> owner, String fieldName, Object value) throws Exception {
        Field f = owner.getDeclaredField(fieldName);
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
        inject(mapper, CategoryMapperImpl.class, "targetMapper", targetMapper);

        CategoryDTO dto = mapper.toDto(child);

        assertEquals(child.getId(), dto.getId());
        assertEquals(child.getLabel(), dto.getLabel());
        assertEquals(child.getTransactionType(), dto.getTransactionType());
        assertEquals(child.getSmoothType(), dto.getSmoothType());
        assertEquals(child.getIcon(), dto.getIcon());
        assertEquals(parent.getId(), dto.getParentId());
        assertNotNull(dto.getTargets());
        assertEquals(1, dto.getTargets().size());
        TargetDTO t = dto.getTargets().get(0);
        assertEquals(target.getId(), t.getId());
        assertEquals(child.getId(), t.getCategoryId());
        assertEquals(target.getAmount(), t.getAmount());
    }

    @Test
    void testToDtoNoParent() throws Exception {
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
        assertNull(mapper.toEntity(null));
    }

    @Test
    void testNullTargetLists() throws Exception {
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        Category c = new Category();
        c.setId(1L);
        c.setTargets(null);
        CategoryDTO dto = mapper.toDto(c);
        assertNull(dto.getTargets());

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setTargets(null);
        Category entity = mapper.toEntity(dto2);
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
        inject(targetMapper, TargetMapper.class, "categoryRepository", repo);

        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, CategoryMapper.class, "categoryRepository", repo);
        inject(mapper, CategoryMapperImpl.class, "targetMapper", targetMapper);

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

        Category entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getLabel(), entity.getLabel());
        assertEquals(dto.getTransactionType(), entity.getTransactionType());
        assertEquals(dto.getSmoothType(), entity.getSmoothType());
        assertEquals(dto.getIcon(), entity.getIcon());
        assertSame(parent, entity.getParent());
        assertNotNull(entity.getTargets());
        assertEquals(1, entity.getTargets().size());
        Target mappedTarget = entity.getTargets().get(0);
        assertEquals(targetDTO.getId(), mappedTarget.getId());
        assertEquals(targetDTO.getAmount(), mappedTarget.getAmount());
        assertSame(childFromRepo, mappedTarget.getCategory());
        verify(repo).findById(1L);
        verify(repo).findById(2L);
    }

    @Test
    void testToEntityMissingParent() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        when(repo.findById(1L)).thenReturn(Optional.empty());
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, CategoryMapper.class, "categoryRepository", repo);
        CategoryDTO dto = new CategoryDTO();
        dto.setParentId(1L);

        Category entity = mapper.toEntity(dto);
        assertNull(entity.getParent());
        verify(repo).findById(1L);
    }

    @Test
    void testToEntityNullFields() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, CategoryMapper.class, "categoryRepository", repo);

        CategoryDTO dto = new CategoryDTO();
        dto.setTargets(List.of());

        Category entity = mapper.toEntity(dto);
        assertNull(entity.getParent());
        assertNotNull(entity.getTargets());
        assertTrue(entity.getTargets().isEmpty());
        verifyNoInteractions(repo);
    }

    @Test
    void testMappingHelpers() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        CategoryMapperImpl mapper = new CategoryMapperImpl();
        inject(mapper, CategoryMapper.class, "categoryRepository", repo);

        Category parent = new Category();
        parent.setId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(parent));

        Method toParent = CategoryMapper.class.getDeclaredMethod("mapParentIdToParent", Long.class);
        toParent.setAccessible(true);
        assertSame(parent, toParent.invoke(mapper, 5L));
        assertNull(toParent.invoke(mapper, (Object) null));
        Method toId = CategoryMapper.class.getDeclaredMethod("mapParentToParentId", Category.class);
        toId.setAccessible(true);
        assertEquals(5L, toId.invoke(mapper, parent));
        assertNull(toId.invoke(mapper, (Object) null));
    }
}
