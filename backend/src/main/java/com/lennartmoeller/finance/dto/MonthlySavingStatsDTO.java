package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;
import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlySavingStatsDTO {
    @Nonnull
    private YearMonth yearMonth;

    @Nonnull
    private StatsMetricDTO balanceChange;

    @Nonnull
    private StatsMetricDTO balanceChangeTarget;

    @Nonnull
    private Long deposits;

    @Nonnull
    private Double depositsTarget;

    @Nonnull
    private Double inflationLoss;

    @Nonnull
    private Double investmentRevenue;

    @JsonProperty
    public StatsMetricDTO getBalanceChangeDeviation() {
        StatsMetricDTO output = new StatsMetricDTO();
        output.setRaw(this.balanceChange.getRaw() - this.balanceChangeTarget.getRaw());
        output.setSmoothed(this.balanceChange.getSmoothed() - this.balanceChangeTarget.getSmoothed());
        return output;
    }

    @JsonProperty
    public Double getDepositsDeviation() {
        return this.deposits - this.depositsTarget;
    }

    @JsonProperty
    public Double getInflationImpact() {
        return this.investmentRevenue + this.inflationLoss;
    }
}
