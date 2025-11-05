import TypeMapper from "@/mapper/TypeMapper";
import Category, { CategoryDTO, categoryMapper } from "@/types/Category";
import RowStats, { RowStatsDTO, rowStatsMapper } from "@/types/RowStats";

interface CategoryStats {
    category: Category;
    stats: RowStats;
    children: Array<CategoryStats>;
}

export interface CategoryStatsDTO {
    category: CategoryDTO;
    stats: RowStatsDTO;
    children: Array<CategoryStatsDTO>;
}

export const categoryStatsMapper: TypeMapper<CategoryStats, CategoryStatsDTO> = {
    fromDTO: (dto: CategoryStatsDTO) => ({
        category: categoryMapper.fromDTO(dto.category),
        stats: rowStatsMapper.fromDTO(dto.stats),
        children: dto.children.map(categoryStatsMapper.fromDTO),
    }),
    toDTO: (model: CategoryStats) => ({
        category: categoryMapper.toDTO(model.category),
        stats: rowStatsMapper.toDTO(model.stats),
        children: model.children.map(categoryStatsMapper.toDTO),
    }),
};

export default CategoryStats;
