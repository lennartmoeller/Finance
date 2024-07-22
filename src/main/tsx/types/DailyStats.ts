import {dateMapper, TypeMapper} from "@/mapper/mappings";

export interface DailyStats {
    date: Date;
    balance: number;
    smoothedBalance: number;
}

export interface DailyStatsDTO {
    date: string;
    balance: number;
    smoothedBalance: number;
}

export const dailyStatsMapper: TypeMapper<DailyStats, DailyStatsDTO> = {
    fromDTO: (dto: DailyStatsDTO) => ({
        ...dto,
        date: dateMapper.fromDTO(dto.date),
    }),
    toDTO: (model: DailyStats) => ({
        ...model,
        date: dateMapper.toDTO(model.date),
    }),
};
