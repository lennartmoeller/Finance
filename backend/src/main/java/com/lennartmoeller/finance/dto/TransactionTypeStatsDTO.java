package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lennartmoeller.finance.util.DateRange;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class TransactionTypeStatsDTO {
    private final List<CategoryStatsDTO> categoryStats;

    @JsonIgnore
    private final @Nullable DateRange dateRange;

    public static TransactionTypeStatsDTO empty(@Nullable DateRange dateRange) {
        return new TransactionTypeStatsDTO(List.of(), dateRange);
    }

    @JsonProperty
    public RowStatsDTO getTotalStats() {
        if (this.dateRange == null) {
            return RowStatsDTO.empty(null);
        }

        if (this.getCategoryStats().isEmpty()) {
            return RowStatsDTO.empty(dateRange);
        }

        Map<YearMonth, CellStatsDTO> totalMonthly = this.getCategoryStats().stream()
                .flatMap(categoryStatsDTO -> categoryStatsDTO.getStats().getMonthly().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> CellStatsDTO.add(entry.getValue())));

        return new RowStatsDTO(totalMonthly);
    }
}
