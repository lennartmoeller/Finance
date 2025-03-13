import React from "react";

import {useMonthlySavingStats} from "@/services/monthlySavingStats";
import LineChart, {LineChartDataPoint} from "@/views/DashboardView/Charts/LineChart";

const InflationCompensationChart: React.FC = () => {
    const {data: statsData} = useMonthlySavingStats();

    if (!statsData) return null;

    let value = 0;
    const chartData: Array<LineChartDataPoint> = statsData.map(stat => {
        value += stat.inflationImpact;
        return {
            label: stat.yearMonth.toLabel(),
            value,
            target: 0,
        };
    });

    return (
        <LineChart data={chartData} title="Inflation Compensation"/>
    );
};

export default InflationCompensationChart;
