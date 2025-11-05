import React from "react";

import { useMonthlySavingStats } from "@/services/monthlySavingStats";
import LineChart, { LineChartDataPoint } from "@/views/DashboardView/Charts/LineChart";

const TotalDepositsChart: React.FC = () => {
    const { data: statsData } = useMonthlySavingStats();

    if (!statsData) return null;

    let value: number = 0;
    let target: number = 0;
    const chartData: Array<LineChartDataPoint> = statsData.map((stat) => {
        value += stat.deposits;
        target += stat.depositsTarget;
        return {
            label: stat.yearMonth.toLabel(),
            value,
            target,
        };
    });

    return <LineChart data={chartData} title="Total Deposits" />;
};

export default TotalDepositsChart;
