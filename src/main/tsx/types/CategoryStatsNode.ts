import TypeMapper from "@/mapper/TypeMapper";
import Category, {CategoryDTO, categoryMapper} from "@/types/Category";
import MonthlyStats, {MonthlyStatsDTO, monthlyStatsMapper} from "@/types/MonthlyStats";

interface CategoryStatsNode {
    category: Category;
    statistics: Record<string, MonthlyStats>;
    children: CategoryStatsNode[];
}

export interface CategoryStatsNodeDTO {
    category: CategoryDTO;
    statistics: MonthlyStatsDTO[];
    children: CategoryStatsNodeDTO[];
}

export const categoryStatsNodeMapper: TypeMapper<CategoryStatsNode, CategoryStatsNodeDTO> = {
    fromDTO: (dto: CategoryStatsNodeDTO) => ({
        category: categoryMapper.fromDTO(dto.category),
        statistics: dto.statistics.reduce((acc, stats) => {
            acc[stats.month] = monthlyStatsMapper.fromDTO(stats);
            return acc;
        }, {} as Record<string, MonthlyStats>),
        children: dto.children.map(categoryStatsNodeMapper.fromDTO),
    }),
    toDTO: (model: CategoryStatsNode) => ({
        category: categoryMapper.toDTO(model.category),
        statistics: Object.values(model.statistics).map(monthlyStatsMapper.toDTO),
        children: model.children.map(categoryStatsNodeMapper.toDTO),
    }),
};

export default CategoryStatsNode;
