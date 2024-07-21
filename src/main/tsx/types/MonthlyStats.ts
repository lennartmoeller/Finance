export interface MonthlyStats {
    month: Date;
    surplus: number;
    smoothedSurplus: number;
    target: number;
    deviation: number;
}
