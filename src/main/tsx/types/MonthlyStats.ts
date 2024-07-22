import {TypeMapper} from "@/mapper/mappings";

export interface MonthlyStats {
    month: string; // TODO: Not the right type
    surplus: number;
    smoothedSurplus: number;
    target: number;
    deviation: number;
}

export interface MonthlyStatsDTO {
    month: string;
    surplus: number;
    smoothedSurplus: number;
    target: number;
    deviation: number;
}

export const monthlyStatsMapper: TypeMapper<MonthlyStats, MonthlyStatsDTO> = {
    fromDTO: (dto: MonthlyStatsDTO) => dto,
    toDTO: (model: MonthlyStats) => model,
};
