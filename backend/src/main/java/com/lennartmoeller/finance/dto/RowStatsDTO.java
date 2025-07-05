package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lennartmoeller.finance.util.DateRange;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class RowStatsDTO {
    @Nonnull
    private final Map<YearMonth, CellStatsDTO> monthly;

    public static RowStatsDTO empty(@Nullable DateRange dateRange) {
        if (dateRange == null) {
            return new RowStatsDTO(Map.of());
        }

        HashMap<YearMonth, CellStatsDTO> monthly = dateRange
                .createMonthStream()
                .collect(HashMap::new, (map, month) -> map.put(month, CellStatsDTO.empty()), HashMap::putAll);

        return new RowStatsDTO(monthly);
    }

    @JsonProperty
    public CellStatsDTO getMean() {
        if (this.getMonthly().isEmpty()) {
            return CellStatsDTO.empty();
        }

        CellStatsDTO mean = new CellStatsDTO();
        mean.setSurplus(StatsMetricDTO.mean(this.getMonthly().values().stream()
                .map(CellStatsDTO::getSurplus)
                .toList()));
        mean.setTarget(0.0); // TODO
        return mean;
    }
}
