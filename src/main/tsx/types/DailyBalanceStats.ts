import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface DailyBalanceStats {
    date: Date;
    balance: StatsMetric;
}

export interface DailyBalanceStatsDTO {
    date: string;
    balance: StatsMetricDTO;
}

export const dailyBalanceStatsMapper: TypeMapper<DailyBalanceStats, DailyBalanceStatsDTO> = {
    fromDTO: (dto: DailyBalanceStatsDTO) => ({
        date: dateMapper.fromDTO(dto.date),
        balance: statsMetricMapper.fromDTO(dto.balance),
    }),
    toDTO: (model: DailyBalanceStats) => ({
        date: dateMapper.toDTO(model.date),
        balance: statsMetricMapper.toDTO(model.balance),
    }),
};

export default DailyBalanceStats;
