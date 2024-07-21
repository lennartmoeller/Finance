import {CategoryDTO} from "@/types/CategoryDTO";
import {MonthlyStatsDTO} from "@/types/MonthlyStatsDTO";

export interface CategoryStatsNodeDTO {
    category: CategoryDTO;
    statistics: MonthlyStatsDTO[];
    children: CategoryStatsNodeDTO[];
}
