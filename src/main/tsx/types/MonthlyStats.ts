import {TypeMapper} from "@/mapper/mappings";
import {StatsMetric, StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";
import {YearMonth} from "@/utils/YearMonth";

export interface MonthlyStats {
    month: YearMonth;
    incomes: StatsMetric;
    expenses: StatsMetric;
    surplus: StatsMetric;
    target: number;
    deviation: StatsMetric;
}

export interface MonthlyStatsDTO {
    month: string;
    incomes: StatsMetricDTO;
    expenses: StatsMetricDTO;
    surplus: StatsMetricDTO;
    target: number;
    deviation: StatsMetricDTO;
}

export const monthlyStatsMapper: TypeMapper<MonthlyStats, MonthlyStatsDTO> = {
    fromDTO: (dto: MonthlyStatsDTO) => ({
        month: YearMonth.fromString(dto.month),
        incomes: statsMetricMapper.fromDTO(dto.incomes),
        expenses: statsMetricMapper.fromDTO(dto.expenses),
        surplus: statsMetricMapper.fromDTO(dto.surplus),
        target: dto.target,
        deviation: statsMetricMapper.fromDTO(dto.deviation),
    }),
    toDTO: (model: MonthlyStats) => ({
        month: model.month.toString(),
        incomes: statsMetricMapper.toDTO(model.incomes),
        expenses: statsMetricMapper.toDTO(model.expenses),
        surplus: statsMetricMapper.toDTO(model.surplus),
        target: model.target,
        deviation: statsMetricMapper.toDTO(model.deviation),
    }),
};
