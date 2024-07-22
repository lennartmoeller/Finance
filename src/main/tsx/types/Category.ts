import {dateMapper, dateMapperNullable, TypeMapper} from "@/mapper/mappings";
import {CategorySmoothType} from "@/types/CategorySmoothType";
import {TransactionType} from "@/types/TransactionType";

export interface Category {
    id: number;
    parentId: number | null;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    start: Date;
    end: Date | null;
    target: number | null;
}

export interface CategoryDTO {
    id: number;
    parentId: number | null;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    start: string;
    end: string | null;
    target: number | null;
}

export const categoryMapper: TypeMapper<Category, CategoryDTO> = {
    fromDTO: (dto: CategoryDTO) => ({
        ...dto,
        start: dateMapper.fromDTO(dto.start),
        end: dateMapperNullable.fromDTO(dto.end),
    }),
    toDTO: (model: Category) => ({
        ...model,
        start: dateMapper.toDTO(model.start),
        end: dateMapperNullable.toDTO(model.end),
    }),
};
