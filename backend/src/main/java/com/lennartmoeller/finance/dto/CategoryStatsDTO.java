package com.lennartmoeller.finance.dto;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryStatsDTO {
    @Nonnull
    private CategoryDTO category;

    @Nonnull
    private RowStatsDTO stats;

    @Nonnull
    private List<CategoryStatsDTO> children;
}
