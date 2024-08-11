import TypeMapper from "@/mapper/TypeMapper";
import CategoryStatsNode, {CategoryStatsNodeDTO, categoryStatsNodeMapper} from "@/types/CategoryStatsNode";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";

interface CategoryStats {
    categoryStats: Array<CategoryStatsNode>;
    totalStats: RowStats;
}

export interface CategoryStatsDTO {
    categoryStats: Array<CategoryStatsNodeDTO>;
    totalStats: RowStatsDTO;
}

export const categoryStatsMapper: TypeMapper<CategoryStats, CategoryStatsDTO> = {
    fromDTO: (dto: CategoryStatsDTO) => ({
        categoryStats: dto.categoryStats.map(categoryStatsNodeMapper.fromDTO),
        totalStats: rowStatsMapper.fromDTO(dto.totalStats),
    }),
    toDTO: (model: CategoryStats) => ({
        categoryStats: model.categoryStats.map(categoryStatsNodeMapper.toDTO),
        totalStats: rowStatsMapper.toDTO(model.totalStats),
    }),
};

export default CategoryStats;
