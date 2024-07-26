import {TypeMapper} from "@/mapper/mappings";

export interface StatsMetric {
    raw: number;
    smoothed: number;
}

export interface StatsMetricDTO {
    raw: number;
    smoothed: number;
}

export const statsMetricMapper: TypeMapper<StatsMetric, StatsMetricDTO> = {
    fromDTO: (dto: StatsMetricDTO) => dto,
    toDTO: (model: StatsMetric) => model,
};
