import TypeMapper from "@/mapper/TypeMapper";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface CellStats {
    surplus: StatsMetric;
    target: number;
    deviation: StatsMetric;
    performance: StatsMetric | null;
}

export interface CellStatsDTO {
    surplus: StatsMetricDTO;
    target: number;
    deviation: StatsMetricDTO;
    performance: StatsMetricDTO | null;
}

export const cellStatsMapper: TypeMapper<CellStats, CellStatsDTO> = {
    fromDTO: (dto: CellStatsDTO) => ({
        ...dto,
        surplus: statsMetricMapper.fromDTO(dto.surplus),
        deviation: statsMetricMapper.fromDTO(dto.deviation),
        performance: dto.performance === null ? null : statsMetricMapper.fromDTO(dto.performance),
    }),
    toDTO: (model: CellStats) => ({
        ...model,
        surplus: statsMetricMapper.toDTO(model.surplus),
        deviation: statsMetricMapper.toDTO(model.deviation),
        performance: model.performance === null ? null : statsMetricMapper.toDTO(model.performance),
    }),
};

export default CellStats;
