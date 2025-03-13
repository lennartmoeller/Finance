import dateMapper from "@/mapper/dateMapper";
import TypeMapper from "@/mapper/TypeMapper";

interface Target {
    id: number;
    categoryId: number;
    start: Date;
    end: Date;
    amount: number;
}

export interface TargetDTO {
    id: number;
    categoryId: number;
    start: string;
    end: string;
    amount: number;
}

export const targetMapper: TypeMapper<Target, TargetDTO> = {
    fromDTO: (dto: TargetDTO) => ({
        ...dto,
        start: dateMapper.fromDTO(dto.start),
        end: dateMapper.fromDTO(dto.end),
    }),
    toDTO: (model: Target) => ({
        ...model,
        start: dateMapper.toDTO(model.start),
        end: dateMapper.toDTO(model.end),
    }),
};

export default Target;
