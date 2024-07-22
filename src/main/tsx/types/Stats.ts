import {dateMapper, TypeMapper} from "@/mapper/mappings";
import {CategoryStatsNode, CategoryStatsNodeDTO, categoryStatsNodeMapper} from "@/types/CategoryStatsNode";
import {DailyStats, DailyStatsDTO, dailyStatsMapper} from "@/types/DailyStats";
import {MonthlyStats, MonthlyStatsDTO, monthlyStatsMapper} from "@/types/MonthlyStats";

export interface Stats {
    dailyStats: DailyStats[];
    categoryStats: CategoryStatsNode[];
    monthlyStats: MonthlyStats[];
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
        monthlyStats: dto.monthlyStats.map(monthlyStatsMapper.fromDTO),
        startDate: dateMapper.fromDTO(dto.startDate),
        endDate: dateMapper.fromDTO(dto.endDate),
    }),
    toDTO: (model: Stats) => ({
        dailyStats: model.dailyStats.map(dailyStatsMapper.toDTO),
        categoryStats: model.categoryStats.map(categoryStatsNodeMapper.toDTO),
        monthlyStats: model.monthlyStats.map(monthlyStatsMapper.toDTO),
        startDate: dateMapper.toDTO(model.startDate),
        endDate: dateMapper.toDTO(model.endDate),
    }),
};
