import dateMapperNullable from "@/mapper/dateMapperNullable";
import TypeMapper from "@/mapper/TypeMapper";
import RowStats, { RowStatsDTO, rowStatsMapper } from "@/types/RowStats";
import TransactionType from "@/types/TransactionType";
import TransactionTypeStats, {
    TransactionTypeStatsDTO,
    transactionTypeStatsMapper,
} from "@/types/TransactionTypeStats";

interface MonthlyCategoryStats {
    stats: Record<TransactionType, TransactionTypeStats>;
    totalStats: RowStats;
    startDate: Date | null;
    endDate: Date | null;
}

export interface MonthlyCategoryStatsDTO {
    stats: Record<TransactionType, TransactionTypeStatsDTO>;
    totalStats: RowStatsDTO;
    startDate: string | null;
    endDate: string | null;
}

export const monthlyCategoryStatsMapper: TypeMapper<
    MonthlyCategoryStats,
    MonthlyCategoryStatsDTO
> = {
    fromDTO: (dto: MonthlyCategoryStatsDTO) => ({
        stats: Object.entries(dto.stats).reduce(
            (acc, [key, value]) => {
                acc[key as TransactionType] =
                    transactionTypeStatsMapper.fromDTO(value);
                return acc;
            },
            {} as Record<TransactionType, TransactionTypeStats>,
        ),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
        startDate: dateMapperNullable.fromDTO(dto.startDate),
        endDate: dateMapperNullable.fromDTO(dto.endDate),
    }),
    toDTO: (model: MonthlyCategoryStats) => ({
        stats: Object.entries(model.stats).reduce(
            (acc, [key, value]) => {
                acc[key as TransactionType] =
                    transactionTypeStatsMapper.toDTO(value);
                return acc;
            },
            {} as Record<TransactionType, TransactionTypeStatsDTO>,
        ),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
        startDate: dateMapperNullable.toDTO(model.startDate),
        endDate: dateMapperNullable.toDTO(model.endDate),
    }),
};

export default MonthlyCategoryStats;
