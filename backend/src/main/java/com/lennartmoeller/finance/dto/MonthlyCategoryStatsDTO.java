package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lennartmoeller.finance.model.TransactionType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class MonthlyCategoryStatsDTO {

    private Map<TransactionType, TransactionTypeStatsDTO> stats;
    private @Nullable LocalDate startDate;
    private @Nullable LocalDate endDate;

    public static MonthlyCategoryStatsDTO empty() {
        MonthlyCategoryStatsDTO monthlyCategoryStatsDTO = new MonthlyCategoryStatsDTO();
        monthlyCategoryStatsDTO.stats = Arrays.stream(TransactionType.values())
                .collect(Collectors.toMap(Function.identity(), transactionType -> TransactionTypeStatsDTO.empty(null)));
        return monthlyCategoryStatsDTO;
    }

    @JsonProperty
    public RowStatsDTO getTotalStats() {
        Map<YearMonth, CellStatsDTO> totalStats = this.stats.values().stream()
                .flatMap(transactionTypeStatsDTO -> transactionTypeStatsDTO.getCategoryStats().stream())
                .flatMap(categoryStatsNodeDTO -> categoryStatsNodeDTO.getStats().getMonthly().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> CellStatsDTO.add(entry.getValue())));

        return new RowStatsDTO(totalStats);
    }
}
