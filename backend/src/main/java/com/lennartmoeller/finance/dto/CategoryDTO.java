package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class CategoryDTO {
    private @Nullable Long id;
    private @Nullable Long parentId;
    private String label;
    private TransactionType transactionType;
    private CategorySmoothType smoothType;
    private @Nullable String icon;
    private List<TargetDTO> targets;
}
