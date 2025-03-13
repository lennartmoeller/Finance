import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface DailySavingStats {
    date: Date;
    balance: StatsMetric;
    target: StatsMetric;
}

export interface DailySavingStatsDTO {
    date: string;
    balance: StatsMetricDTO;
    target: StatsMetricDTO;
}

export const dailySavingStatsMapper: TypeMapper<DailySavingStats, DailySavingStatsDTO> = {
    fromDTO: (dto: DailySavingStatsDTO) => ({
        date: dateMapper.fromDTO(dto.date),
        balance: statsMetricMapper.fromDTO(dto.balance),
        target: statsMetricMapper.fromDTO(dto.target),
    }),
    toDTO: (model: DailySavingStats) => ({
        date: dateMapper.toDTO(model.date),
        balance: statsMetricMapper.toDTO(model.balance),
        target: statsMetricMapper.toDTO(model.target),
    }),
};

export default DailySavingStats;
