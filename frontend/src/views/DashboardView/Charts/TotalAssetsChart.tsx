import React from "react";

import { useDailyBalanceStats } from "@/services/dailyBalanceStats";
import LineChart, { LineChartDataPoint } from "@/views/DashboardView/Charts/LineChart";
import useStatsMode from "@/views/StatsView/stores/useStatsMode";

const TotalAssetsChart: React.FC = () => {
    const { data: dailyBalanceStats } = useDailyBalanceStats();
    const { smoothed } = useStatsMode();

    if (!dailyBalanceStats) return null;

    const chartData: Array<LineChartDataPoint> = dailyBalanceStats.map((stat) => ({
        label: new Date(stat.date).toLocaleDateString("de-DE", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
        }),
        value: stat.balance[smoothed ? "smoothed" : "raw"],
        target: stat.target[smoothed ? "smoothed" : "raw"],
    }));

    return <LineChart data={chartData} title="Total Assets" />;
};

export default TotalAssetsChart;
