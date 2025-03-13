import React from "react";

import {useMonthlyCategoryBalanceStats} from "@/services/monthlyCategoryBalanceStats";
import {useMonthlySavingStats} from "@/services/monthlySavingStats";
import BarChart, {BarChartDataPoint} from "@/views/DashboardView/Charts/BarChart";

const BalanceChangeDeviationChart: React.FC = () => {
    const {data: statsData} = useMonthlySavingStats();
    const {data: advancedStatsData} = useMonthlyCategoryBalanceStats();

    if (!statsData || !advancedStatsData) return null;

    const chartData: Array<BarChartDataPoint<string>> = statsData.map((stat) => {
        return {
            label: stat.yearMonth.toLabel(),
            data: {
                "surplus": stat.balanceChangeDeviation.smoothed,
            },
        };
    });

    const labels = {
        "surplus": "Surplus",
    };

    return (
        <BarChart data={chartData} labels={labels} title="Balance Change Deviation"/>
    );
};

export default BalanceChangeDeviationChart;
