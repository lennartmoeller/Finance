package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {

    private final CategoryMapperImpl mapper = new CategoryMapperImpl();

    @Mock
    private CategoryRepository repository;

    @BeforeEach
    void injectTargetMapper() throws Exception {
        Field f = CategoryMapperImpl.class.getDeclaredField("targetMapper");
        f.setAccessible(true);
        f.set(mapper, new TargetMapperImpl());
    }

    private static Category category(Long id) {
        Category c = new Category();
        c.setId(id);
        c.setLabel("cat" + id);
        c.setTransactionType(TransactionType.EXPENSE);
        c.setSmoothType(CategorySmoothType.MONTHLY);
        c.setIcon("icon");
        return c;
    }

    private static Target target(Category cat) {
        Target t = new Target();
        t.setId(9L);
        t.setCategory(cat);
        t.setStartDate(LocalDate.now());
        t.setAmount(50L);
        return t;
    }

    @Nested
    class ToDto {
        @Test
        void mapsParentAndTargets() {
            Category parent = category(1L);
            Category child = category(2L);
            child.setParent(parent);
            Target target = target(child);
            child.setTargets(List.of(target));

            CategoryDTO dto = mapper.toDto(child);

            assertThat(dto.getParentId()).isEqualTo(parent.getId());
            assertThat(dto.getTargets()).hasSize(1);
            TargetDTO td = dto.getTargets().getFirst();
            assertThat(td.getCategoryId()).isEqualTo(child.getId());
            assertThat(td.getAmount()).isEqualTo(target.getAmount());
        }

        @ParameterizedTest
        @NullSource
        void returnsNullOnNullInput(Category input) {
            assertThat(mapper.toDto(input)).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void resolvesParentAndTargetsUsingRepository() {
            Category parent = category(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(parent));

            Category childEntity = category(2L);
            when(repository.findById(2L)).thenReturn(Optional.of(childEntity));

            TargetDTO targetDto = new TargetDTO();
            targetDto.setId(5L);
            targetDto.setCategoryId(2L);
            targetDto.setStart(LocalDate.now());
            targetDto.setAmount(100L);

            CategoryDTO dto = new CategoryDTO();
            dto.setId(3L);
            dto.setParentId(1L);
            dto.setLabel("child");
            dto.setTransactionType(TransactionType.EXPENSE);
            dto.setSmoothType(CategorySmoothType.MONTHLY);
            dto.setTargets(List.of(targetDto));

            Category entity = mapper.toEntity(dto, repository);

            assertThat(entity.getParent()).isSameAs(parent);
            assertThat(entity.getTargets()).hasSize(1);
            assertThat(entity.getTargets().getFirst().getCategory()).isSameAs(childEntity);
            verify(repository).findById(1L);
            verify(repository).findById(2L);
        }

        @Test
        void missingParentResultsInNull() {
            when(repository.findById(5L)).thenReturn(Optional.empty());
            CategoryDTO dto = new CategoryDTO();
            dto.setParentId(5L);

            Category entity = mapper.toEntity(dto, repository);

            assertThat(entity.getParent()).isNull();
            verify(repository).findById(5L);
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenDtoIsNull(CategoryDTO dto) {
            assertThat(mapper.toEntity(dto, repository)).isNull();
            verifyNoInteractions(repository);
        }
    }

    @Nested
    class MappingHelpers {
        @Test
        void mapParentIdToParentFetchesFromRepo() {
            Category parent = category(8L);
            when(repository.findById(8L)).thenReturn(Optional.of(parent));

            assertThat(mapper.mapParentIdToParent(8L, repository)).isSameAs(parent);
            assertThat(mapper.mapParentIdToParent(null, repository)).isNull();
        }

        @Test
        void mapParentToParentIdReturnsIdOrNull() {
            Category parent = category(10L);
            assertThat(mapper.mapParentToParentId(parent)).isEqualTo(10L);
            assertThat(mapper.mapParentToParentId(null)).isNull();
        }
    }
}
