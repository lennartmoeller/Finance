import {CategoryStatsNode} from "@/types/CategoryStatsNode";
import {DailyStats} from "@/types/DailyStats";
import {MonthlyStats} from "@/types/MonthlyStats";

export interface Stats {
    dailyStats: DailyStats[];
    categoryStats: CategoryStatsNode[];
    monthlyStats: MonthlyStats[];
}
