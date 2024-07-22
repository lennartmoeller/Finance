import {TypeMapper} from "@/mapper/mappings";
import {Category, CategoryDTO, categoryMapper} from "@/types/Category";
import {MonthlyStats, MonthlyStatsDTO, monthlyStatsMapper} from "@/types/MonthlyStats";

export interface CategoryStatsNode {
    category: Category;
    statistics: MonthlyStats[];
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
        statistics: dto.statistics.map(monthlyStatsMapper.fromDTO),
        children: dto.children.map(categoryStatsNodeMapper.fromDTO),
    }),
    toDTO: (model: CategoryStatsNode) => ({
        category: categoryMapper.toDTO(model.category),
        statistics: model.statistics.map(monthlyStatsMapper.toDTO),
        children: model.children.map(categoryStatsNodeMapper.toDTO),
    }),
};
