package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {
    @Nullable
    private Long id;

    @Nullable
    private Long parentId;

    @Nullable
    private String label;

    @Nullable
    private TransactionType transactionType;

    @Nullable
    private CategorySmoothType smoothType;

    @Nullable
    private String icon;

    @Nullable
    private List<TargetDTO> targets;
}
