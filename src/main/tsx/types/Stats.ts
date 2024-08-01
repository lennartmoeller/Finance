import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import CategoryStatsNode, {CategoryStatsNodeDTO, categoryStatsNodeMapper} from "@/types/CategoryStatsNode";
import DailyStats, {DailyStatsDTO, dailyStatsMapper} from "@/types/DailyStats";
import MonthlyStats, {MonthlyStatsDTO, monthlyStatsMapper} from "@/types/MonthlyStats";

interface Stats {
    dailyStats: DailyStats[];
    categoryStats: CategoryStatsNode[];
    monthlyStats: Record<string, MonthlyStats>;
    startDate: Date;
    endDate: Date;
}

export interface StatsDTO {
    dailyStats: DailyStatsDTO[];
    categoryStats: CategoryStatsNodeDTO[];
    monthlyStats: MonthlyStatsDTO[];
    startDate: string;
    endDate: string;
}

export const statsMapper: TypeMapper<Stats, StatsDTO> = {
    fromDTO: (dto: StatsDTO) => ({
        dailyStats: dto.dailyStats.map(dailyStatsMapper.fromDTO),
        categoryStats: dto.categoryStats.map(categoryStatsNodeMapper.fromDTO),
        monthlyStats: dto.monthlyStats.reduce((acc, stats) => {
            acc[stats.month] = monthlyStatsMapper.fromDTO(stats);
            return acc;
        }, {} as Record<string, MonthlyStats>),
        startDate: dateMapper.fromDTO(dto.startDate),
        endDate: dateMapper.fromDTO(dto.endDate),
    }),
    toDTO: (model: Stats) => ({
        dailyStats: model.dailyStats.map(dailyStatsMapper.toDTO),
        categoryStats: model.categoryStats.map(categoryStatsNodeMapper.toDTO),
        monthlyStats: Object.values(model.monthlyStats).map(monthlyStatsMapper.toDTO),
        startDate: dateMapper.toDTO(model.startDate),
        endDate: dateMapper.toDTO(model.endDate),
    }),
};

export default Stats;
