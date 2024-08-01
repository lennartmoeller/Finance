import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface DailyStats {
    date: Date;
    balance: StatsMetric;
}

export interface DailyStatsDTO {
    date: string;
    balance: StatsMetricDTO;
}

export const dailyStatsMapper: TypeMapper<DailyStats, DailyStatsDTO> = {
    fromDTO: (dto: DailyStatsDTO) => ({
        date: dateMapper.fromDTO(dto.date),
        balance: statsMetricMapper.fromDTO(dto.balance),
    }),
    toDTO: (model: DailyStats) => ({
        date: dateMapper.toDTO(model.date),
        balance: statsMetricMapper.toDTO(model.balance),
    }),
};

export default DailyStats;
