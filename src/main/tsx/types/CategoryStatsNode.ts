import TypeMapper from "@/mapper/TypeMapper";
import Category, {CategoryDTO, categoryMapper} from "@/types/Category";
import RowStats, {RowStatsDTO, rowStatsMapper} from "@/types/RowStats";

interface CategoryStatsNode {
    category: Category;
    stats: RowStats;
    children: Array<CategoryStatsNode>;
}

export interface CategoryStatsNodeDTO {
    category: CategoryDTO;
    stats: RowStatsDTO;
    children: Array<CategoryStatsNodeDTO>;
}

export const categoryStatsNodeMapper: TypeMapper<CategoryStatsNode, CategoryStatsNodeDTO> = {
    fromDTO: (dto: CategoryStatsNodeDTO) => ({
        category: categoryMapper.fromDTO(dto.category),
        stats: rowStatsMapper.fromDTO(dto.stats),
        children: dto.children.map(categoryStatsNodeMapper.fromDTO),
    }),
    toDTO: (model: CategoryStatsNode) => ({
        category: categoryMapper.toDTO(model.category),
        stats: rowStatsMapper.toDTO(model.stats),
        children: model.children.map(categoryStatsNodeMapper.toDTO),
    }),
};

export default CategoryStatsNode;
