import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {
    StatsMetricDTO,
    statsMetricMapper,
} from "@/types/StatsMetric";
import YearMonth from "@/utils/YearMonth";

interface MonthlySavingStats {
    yearMonth: YearMonth;
    balanceChange: StatsMetric;
    balanceChangeTarget: StatsMetric;
    balanceChangeDeviation: StatsMetric;
    deposits: number;
    depositsTarget: number;
    depositsDeviation: number;
    inflationLoss: number;
    investmentRevenue: number;
    inflationImpact: number;
}

export interface MonthlySavingStatsDTO {
    yearMonth: string;
    balanceChange: StatsMetricDTO;
    balanceChangeTarget: StatsMetricDTO;
    balanceChangeDeviation: StatsMetricDTO;
    deposits: number;
    depositsTarget: number;
    depositsDeviation: number;
    inflationLoss: number;
    investmentRevenue: number;
    inflationImpact: number;
}

export const monthlySavingStatsMapper: TypeMapper<
    MonthlySavingStats,
    MonthlySavingStatsDTO
> = {
    fromDTO: (dto: MonthlySavingStatsDTO) => ({
        ...dto,
        yearMonth: YearMonth.fromString(dto.yearMonth),
        balanceChange: statsMetricMapper.fromDTO(dto.balanceChange),
        balanceChangeTarget: statsMetricMapper.fromDTO(dto.balanceChangeTarget),
        balanceChangeDeviation: statsMetricMapper.fromDTO(
            dto.balanceChangeDeviation,
        ),
    }),
    toDTO: (model: MonthlySavingStats) => ({
        ...model,
        yearMonth: YearMonth.toString(model.yearMonth),
        balanceChange: statsMetricMapper.toDTO(model.balanceChange),
        balanceChangeTarget: statsMetricMapper.toDTO(model.balanceChangeTarget),
        balanceChangeDeviation: statsMetricMapper.toDTO(
            model.balanceChangeDeviation,
        ),
    }),
};

export default MonthlySavingStats;
