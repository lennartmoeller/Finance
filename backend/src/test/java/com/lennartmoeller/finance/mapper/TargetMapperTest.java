package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TargetMapperTest {

    private final TargetMapperImpl mapper = new TargetMapperImpl();

    @Mock
    private CategoryRepository repository;

    private static Category category() {
        Category c = new Category();
        c.setId(7L);
        return c;
    }

    private static Target target() {
        Target t = new Target();
        t.setId(5L);
        t.setCategory(category());
        t.setStartDate(LocalDate.of(2024, 1, 1));
        t.setAmount(100L);
        return t;
    }

    @Nested
    class ToDto {
        @Test
        void mapsFields() {
            Target target = target();

            TargetDTO dto = mapper.toDto(target);

            assertThat(dto.getId()).isEqualTo(target.getId());
            assertThat(dto.getCategoryId()).isEqualTo(target.getCategory().getId());
            assertThat(dto.getStart()).isEqualTo(target.getStartDate());
            assertThat(dto.getAmount()).isEqualTo(target.getAmount());
        }

        @ParameterizedTest
        @NullSource
        void returnsNullOnNullInput(Target t) {
            assertThat(mapper.toDto(t)).isNull();
        }

        @Test
        void handlesMissingCategory() {
            Target target = target();
            target.setCategory(null);

            TargetDTO dto = mapper.toDto(target);

            assertThat(dto.getCategoryId()).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void resolvesCategoryUsingRepository() {
            Category cat = category();
            when(repository.findById(7L)).thenReturn(Optional.of(cat));

            TargetDTO dto = new TargetDTO();
            dto.setId(5L);
            dto.setCategoryId(7L);
            dto.setStart(LocalDate.of(2024, 1, 1));
            dto.setAmount(50L);

            Target entity = mapper.toEntity(dto, repository);

            assertThat(entity.getCategory()).isSameAs(cat);
            assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
            verify(repository).findById(7L);
        }

        @Test
        void missingCategoryResultsInNull() {
            when(repository.findById(9L)).thenReturn(Optional.empty());
            TargetDTO dto = new TargetDTO();
            dto.setCategoryId(9L);

            Target entity = mapper.toEntity(dto, repository);

            assertThat(entity.getCategory()).isNull();
            verify(repository).findById(9L);
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenDtoIsNull(TargetDTO dto) {
            assertThat(mapper.toEntity(dto, repository)).isNull();
            verifyNoInteractions(repository);
        }
    }

    @Nested
    class MappingHelpers {
        @Test
        void mapCategoryIdToCategoryFetchesFromRepo() {
            Category cat = category();
            when(repository.findById(7L)).thenReturn(Optional.of(cat));

            assertThat(mapper.mapCategoryIdToCategory(7L, repository)).isSameAs(cat);
            assertThat(mapper.mapCategoryIdToCategory(null, repository)).isNull();
            when(repository.findById(8L)).thenReturn(Optional.empty());
            assertThat(mapper.mapCategoryIdToCategory(8L, repository)).isNull();
        }

        @Test
        void mapCategoryToCategoryIdReturnsIdOrNull() {
            Category cat = category();
            assertThat(mapper.mapCategoryToCategoryId(cat)).isEqualTo(7L);
            assertThat(mapper.mapCategoryToCategoryId(null)).isNull();
        }
    }
}
