package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class MonthlySavingStatsDTO {
    private YearMonth yearMonth;
    private StatsMetricDTO balanceChange;
    private StatsMetricDTO balanceChangeTarget;
    private Long deposits;
    private Double depositsTarget;
    private Double inflationLoss;
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
