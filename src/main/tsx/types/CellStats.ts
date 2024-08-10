import TypeMapper from "@/mapper/TypeMapper";
import Performance, {PerformanceDTO, performanceMapper} from "@/types/Performance";
import StatsMetric, {StatsMetricDTO, statsMetricMapper} from "@/types/StatsMetric";

interface CellStats {
    surplus: StatsMetric;
    target: number;
    deviation: StatsMetric;
    performance: Performance | null;
}

export interface CellStatsDTO {
    surplus: StatsMetricDTO;
    target: number;
    deviation: StatsMetricDTO;
    performance: PerformanceDTO | null;
}

export const cellStatsMapper: TypeMapper<CellStats, CellStatsDTO> = {
    fromDTO: (dto: CellStatsDTO) => ({
        ...dto,
        surplus: statsMetricMapper.fromDTO(dto.surplus),
        deviation: statsMetricMapper.fromDTO(dto.deviation),
        performance: dto.performance === null ? null : performanceMapper.fromDTO(dto.performance),
    }),
    toDTO: (model: CellStats) => ({
        ...model,
        surplus: statsMetricMapper.toDTO(model.surplus),
        deviation: statsMetricMapper.toDTO(model.deviation),
        performance: model.performance === null ? null : performanceMapper.toDTO(model.performance),
    }),
};

export default CellStats;
