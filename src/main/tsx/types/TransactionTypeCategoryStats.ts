import TypeMapper from "@/mapper/TypeMapper";
import CategoryStats, {CategoryStatsDTO, categoryStatsMapper} from "@/types/CategoryStats";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";
import TransactionType from "@/types/TransactionType";

interface TransactionTypeCategoryStats {
    transactionType: TransactionType;
    categoryStats: Array<CategoryStats>;
    totalStats: RowStats;
}

export interface TransactionTypeCategoryStatsDTO {
    transactionType: TransactionType;
    categoryStats: Array<CategoryStatsDTO>;
    totalStats: RowStatsDTO;
}

export const transactionTypeCategoryStatsMapper: TypeMapper<TransactionTypeCategoryStats, TransactionTypeCategoryStatsDTO> = {
    fromDTO: (dto: TransactionTypeCategoryStatsDTO) => ({
        transactionType: dto.transactionType,
        categoryStats: dto.categoryStats.map(categoryStatsMapper.fromDTO),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
    }),
    toDTO: (model: TransactionTypeCategoryStats) => ({
        transactionType: model.transactionType,
        categoryStats: model.categoryStats.map(categoryStatsMapper.toDTO),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
    }),
};

export default TransactionTypeCategoryStats;
