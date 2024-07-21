import {CategoryStatsNodeDTO} from "@/types/CategoryStatsNodeDTO";
import {DailyStatsDTO} from "@/types/DailyStatsDTO";
import {MonthlyStatsDTO} from "@/types/MonthlyStatsDTO";

export interface StatsDTO {
    dailyStats: DailyStatsDTO[];
    categoryStats: CategoryStatsNodeDTO[];
    monthlyStats: MonthlyStatsDTO[];
}
