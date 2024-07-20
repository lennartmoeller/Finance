/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2024-07-20 23:17:51.

export interface AccountDTO {
    id: number;
    label: string;
    startBalance: number;
    active: boolean;
}

export interface CategoryDTO {
    id: number;
    parentId: number;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    start: Date;
    end: Date;
    target: number;
}

export interface StatsDTO {
    dailyStats: DailyStatsDTO[];
    categoryStats: CategoryStatsNodeDTO[];
    monthlyStats: MonthlyStatsDTO[];
}

export interface TransactionDTO {
    id: number;
    accountId: number;
    categoryId: number;
    date: Date;
    amount: number;
    description: string;
}

export interface DailyStatsDTO {
    date: Date;
    balance: number;
    smoothedBalance: number;
}

export interface CategoryStatsNodeDTO {
    category: CategoryDTO;
    statistics: MonthlyStatsDTO[];
    children: CategoryStatsNodeDTO[];
}

export interface MonthlyStatsDTO {
    month: Date;
    surplus: number;
    smoothedSurplus: number;
    target: number;
    deviation: number;
}

export type TransactionType = "EXPENSE" | "INCOME";

export type CategorySmoothType = "DAILY" | "MONTHLY" | "QUARTER_YEARLY" | "HALF_YEARLY" | "YEARLY";
