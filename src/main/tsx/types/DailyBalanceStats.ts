import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface DailyBalanceStats {
    date: Date;
    balance: StatsMetric;
    target: StatsMetric;
}

export interface DailyBalanceStatsDTO {
    date: string;
    balance: StatsMetricDTO;
    target: StatsMetricDTO;
}

export const dailyBalanceStatsMapper: TypeMapper<DailyBalanceStats, DailyBalanceStatsDTO> = {
    fromDTO: (dto: DailyBalanceStatsDTO) => ({
        date: dateMapper.fromDTO(dto.date),
        balance: statsMetricMapper.fromDTO(dto.balance),
        target: statsMetricMapper.fromDTO(dto.target),
    }),
    toDTO: (model: DailyBalanceStats) => ({
        date: dateMapper.toDTO(model.date),
        balance: statsMetricMapper.toDTO(model.balance),
        target: statsMetricMapper.toDTO(model.target),
    }),
};

export default DailyBalanceStats;
