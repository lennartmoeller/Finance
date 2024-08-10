import TypeMapper from "@/mapper/TypeMapper";

interface Performance {
    raw: number;
    smoothed: number;
}

export interface PerformanceDTO {
    raw: number;
    smoothed: number;
}

export const performanceMapper: TypeMapper<Performance, PerformanceDTO> = {
    fromDTO: (dto: PerformanceDTO) => dto,
    toDTO: (model: Performance) => model,
};

export default Performance;
