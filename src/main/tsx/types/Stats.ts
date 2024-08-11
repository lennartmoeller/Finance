import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import CategoryStats, {CategoryStatsDTO, categoryStatsMapper} from "@/types/CategoryStats";
import DailyStats, {DailyStatsDTO, dailyStatsMapper} from "@/types/DailyStats";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";

interface Stats {
    dailyStats: Array<DailyStats>;
    incomeStats: CategoryStats;
    expenseStats: CategoryStats;
    totalStats: RowStats;
    startDate: Date;
    endDate: Date;
}

export interface StatsDTO {
    dailyStats: Array<DailyStatsDTO>;
    incomeStats: CategoryStatsDTO;
    expenseStats: CategoryStatsDTO;
    totalStats: RowStatsDTO;
    startDate: string;
    endDate: string;
}

export const statsMapper: TypeMapper<Stats, StatsDTO> = {
    fromDTO: (dto: StatsDTO) => ({
        dailyStats: dto.dailyStats.map(dailyStatsMapper.fromDTO),
        incomeStats: categoryStatsMapper.fromDTO(dto.incomeStats),
        expenseStats: categoryStatsMapper.fromDTO(dto.expenseStats),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
        startDate: dateMapper.fromDTO(dto.startDate),
        endDate: dateMapper.fromDTO(dto.endDate),
    }),
    toDTO: (model: Stats) => ({
        dailyStats: model.dailyStats.map(dailyStatsMapper.toDTO),
        incomeStats: categoryStatsMapper.toDTO(model.incomeStats),
        expenseStats: categoryStatsMapper.toDTO(model.expenseStats),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
        startDate: dateMapper.toDTO(model.startDate),
        endDate: dateMapper.toDTO(model.endDate),
    }),
};

export default Stats;
