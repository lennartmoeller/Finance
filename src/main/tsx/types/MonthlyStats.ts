import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";
import YearMonth from "@/utils/YearMonth";

interface MonthlyStats {
    month: YearMonth;
    incomes: StatsMetric;
    expenses: StatsMetric;
    surplus: StatsMetric;
    target: number;
    deviation: StatsMetric;
    performance: StatsMetric | null;
}

export interface MonthlyStatsDTO {
    month: string;
    incomes: StatsMetricDTO;
    expenses: StatsMetricDTO;
    surplus: StatsMetricDTO;
    target: number;
    deviation: StatsMetricDTO;
    performance: StatsMetricDTO | null;
}

export const monthlyStatsMapper: TypeMapper<MonthlyStats, MonthlyStatsDTO> = {
    fromDTO: (dto: MonthlyStatsDTO) => ({
        ...dto,
        month: YearMonth.fromString(dto.month),
        incomes: statsMetricMapper.fromDTO(dto.incomes),
        expenses: statsMetricMapper.fromDTO(dto.expenses),
        surplus: statsMetricMapper.fromDTO(dto.surplus),
        deviation: statsMetricMapper.fromDTO(dto.deviation),
        performance: dto.performance === null ? null : statsMetricMapper.fromDTO(dto.performance),
    }),
    toDTO: (model: MonthlyStats) => ({
        ...model,
        month: model.month.toString(),
        incomes: statsMetricMapper.toDTO(model.incomes),
        expenses: statsMetricMapper.toDTO(model.expenses),
        surplus: statsMetricMapper.toDTO(model.surplus),
        deviation: statsMetricMapper.toDTO(model.deviation),
        performance: model.performance === null ? null : statsMetricMapper.toDTO(model.performance),
    }),
};

export default MonthlyStats;
