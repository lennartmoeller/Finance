export interface MonthlyStatsDTO {
    month: string;
    surplus: number;
    smoothedSurplus: number;
    target: number;
    deviation: number;
}
