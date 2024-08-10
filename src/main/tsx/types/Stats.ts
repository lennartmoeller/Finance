import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";
import CategoryStatsNode, {CategoryStatsNodeDTO, categoryStatsNodeMapper} from "@/types/CategoryStatsNode";
import DailyStats, {DailyStatsDTO, dailyStatsMapper} from "@/types/DailyStats";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";

interface Stats {
    dailyStats: DailyStats[];
    categoryStats: CategoryStatsNode[];
    incomeStats: RowStats;
    expenseStats: RowStats;
    surplusStats: RowStats;
    startDate: Date;
    endDate: Date;
}

export interface StatsDTO {
    dailyStats: DailyStatsDTO[];
    categoryStats: CategoryStatsNodeDTO[];
    incomeStats: RowStatsDTO;
    expenseStats: RowStatsDTO;
    surplusStats: RowStatsDTO;
    startDate: string;
    endDate: string;
}

export const statsMapper: TypeMapper<Stats, StatsDTO> = {
    fromDTO: (dto: StatsDTO) => ({
        dailyStats: dto.dailyStats.map(dailyStatsMapper.fromDTO),
        categoryStats: dto.categoryStats.map(categoryStatsNodeMapper.fromDTO),
        incomeStats: rowStatsMapper.fromDTO(dto.incomeStats),
        expenseStats: rowStatsMapper.fromDTO(dto.expenseStats),
        surplusStats: rowStatsMapper.fromDTO(dto.surplusStats),
        startDate: dateMapper.fromDTO(dto.startDate),
        endDate: dateMapper.fromDTO(dto.endDate),
    }),
    toDTO: (model: Stats) => ({
        dailyStats: model.dailyStats.map(dailyStatsMapper.toDTO),
        categoryStats: model.categoryStats.map(categoryStatsNodeMapper.toDTO),
        incomeStats: rowStatsMapper.toDTO(model.incomeStats),
        expenseStats: rowStatsMapper.toDTO(model.expenseStats),
        surplusStats: rowStatsMapper.toDTO(model.surplusStats),
        startDate: dateMapper.toDTO(model.startDate),
        endDate: dateMapper.toDTO(model.endDate),
    }),
};

export default Stats;
