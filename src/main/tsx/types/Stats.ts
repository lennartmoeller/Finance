import {TypeMapper} from "@/mapper/mappings";
import {CategoryStatsNode, CategoryStatsNodeDTO, categoryStatsNodeMapper} from "@/types/CategoryStatsNode";
import {DailyStats, DailyStatsDTO, dailyStatsMapper} from "@/types/DailyStats";
import {MonthlyStats, MonthlyStatsDTO, monthlyStatsMapper} from "@/types/MonthlyStats";

export interface Stats {
    dailyStats: DailyStats[];
    categoryStats: CategoryStatsNode[];
    monthlyStats: MonthlyStats[];
}

export interface StatsDTO {
    dailyStats: DailyStatsDTO[];
    categoryStats: CategoryStatsNodeDTO[];
    monthlyStats: MonthlyStatsDTO[];
}

export const statsMapper: TypeMapper<Stats, StatsDTO> = {
    fromDTO: (dto: StatsDTO) => ({
        dailyStats: dto.dailyStats.map(dailyStatsMapper.fromDTO),
        categoryStats: dto.categoryStats.map(categoryStatsNodeMapper.fromDTO),
        monthlyStats: dto.monthlyStats.map(monthlyStatsMapper.fromDTO),
    }),
    toDTO: (model: Stats) => ({
        dailyStats: model.dailyStats.map(dailyStatsMapper.toDTO),
        categoryStats: model.categoryStats.map(categoryStatsNodeMapper.toDTO),
        monthlyStats: model.monthlyStats.map(monthlyStatsMapper.toDTO),
    }),
};
