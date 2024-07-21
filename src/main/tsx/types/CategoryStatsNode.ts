import {Category} from "@/types/Category";
import {MonthlyStats} from "@/types/MonthlyStats";

export interface CategoryStatsNode {
    category: Category;
    statistics: MonthlyStats[];
    children: CategoryStatsNode[];
}
