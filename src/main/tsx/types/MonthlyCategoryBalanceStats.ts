import dateMapperNullable from "@/mapper/dateMapperNullable";
import TypeMapper from "@/mapper/TypeMapper";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";
import TransactionTypeCategoryStats, {
    TransactionTypeCategoryStatsDTO,
    transactionTypeCategoryStatsMapper
} from "@/types/TransactionTypeCategoryStats";

interface MonthlyCategoryBalanceStats {
    stats: Array<TransactionTypeCategoryStats>;
    totalStats: RowStats;
    startDate: Date | null;
    endDate: Date | null;
}

export interface MonthlyCategoryBalanceStatsDTO {
    stats: Array<TransactionTypeCategoryStatsDTO>;
    totalStats: RowStatsDTO;
    startDate: string | null;
    endDate: string | null;
}

export const monthlyCategoryBalanceStatsMapper: TypeMapper<MonthlyCategoryBalanceStats, MonthlyCategoryBalanceStatsDTO> = {
    fromDTO: (dto: MonthlyCategoryBalanceStatsDTO) => ({
        stats: dto.stats.map(transactionTypeCategoryStatsMapper.fromDTO),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
        startDate: dateMapperNullable.fromDTO(dto.startDate),
        endDate: dateMapperNullable.fromDTO(dto.endDate),
    }),
    toDTO: (model: MonthlyCategoryBalanceStats) => ({
        stats: model.stats.map(transactionTypeCategoryStatsMapper.toDTO),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
        startDate: dateMapperNullable.toDTO(model.startDate),
        endDate: dateMapperNullable.toDTO(model.endDate),
    }),
};

export default MonthlyCategoryBalanceStats;
