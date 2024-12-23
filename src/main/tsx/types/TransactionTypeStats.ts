import TypeMapper from "@/mapper/TypeMapper";
import CategoryStats, {CategoryStatsDTO, categoryStatsMapper} from "@/types/CategoryStats";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";

interface TransactionTypeStats {
    categoryStats: Array<CategoryStats>;
    totalStats: RowStats;
}

export interface TransactionTypeStatsDTO {
    categoryStats: Array<CategoryStatsDTO>;
    totalStats: RowStatsDTO;
}

export const transactionTypeStatsMapper: TypeMapper<TransactionTypeStats, TransactionTypeStatsDTO> = {
    fromDTO: (dto: TransactionTypeStatsDTO) => ({
        categoryStats: dto.categoryStats.map(categoryStatsMapper.fromDTO),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
    }),
    toDTO: (model: TransactionTypeStats) => ({
        categoryStats: model.categoryStats.map(categoryStatsMapper.toDTO),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
    }),
};

export default TransactionTypeStats;
