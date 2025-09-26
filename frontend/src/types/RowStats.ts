import TypeMapper from "@/mapper/TypeMapper";
import CellStats, { CellStatsDTO, cellStatsMapper } from "@/types/CellStats";

interface RowStats {
    monthly: Record<string, CellStats>;
    mean: CellStats;
}

export interface RowStatsDTO {
    monthly: Record<string, CellStatsDTO>;
    mean: CellStatsDTO;
}

export const rowStatsMapper: TypeMapper<RowStats, RowStatsDTO> = {
    fromDTO: (dto: RowStatsDTO) => ({
        monthly: Object.entries(dto.monthly).reduce(
            (acc, [key, value]) => {
                acc[key] = cellStatsMapper.fromDTO(value);
                return acc;
            },
            {} as Record<string, CellStats>,
        ),
        mean: cellStatsMapper.fromDTO(dto.mean),
    }),
    toDTO: (model: RowStats) => ({
        monthly: Object.entries(model.monthly).reduce(
            (acc, [key, value]) => {
                acc[key] = cellStatsMapper.toDTO(value);
                return acc;
            },
            {} as Record<string, CellStatsDTO>,
        ),
        mean: cellStatsMapper.toDTO(model.mean),
    }),
};

export default RowStats;
