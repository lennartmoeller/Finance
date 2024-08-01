import TypeMapper from "@/mapper/TypeMapper";
import CategorySmoothType from "@/types/CategorySmoothType";
import Target, {TargetDTO, targetMapper} from "@/types/Target";
import TransactionType from "@/types/TransactionType";

interface Category {
    id: number;
    parentId: number | null;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    targets: Array<Target>;
}

export interface CategoryDTO {
    id: number;
    parentId: number | null;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    targets: Array<TargetDTO>;
}

export const categoryMapper: TypeMapper<Category, CategoryDTO> = {
    fromDTO: (dto: CategoryDTO) => ({
        ...dto,
        targets: dto.targets.map(targetMapper.fromDTO),
    }),
    toDTO: (model: Category) => ({
        ...model,
        targets: model.targets.map(targetMapper.toDTO),
    }),
};

export default Category;
