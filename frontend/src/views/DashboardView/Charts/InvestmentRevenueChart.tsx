import React from "react";

import {useMonthlySavingStats} from "@/services/monthlySavingStats";
import BarChart, {BarChartDataPoint} from "@/views/DashboardView/Charts/BarChart";

const InvestmentRevenueChart: React.FC = () => {
    const {data: statsData} = useMonthlySavingStats();

    if (!statsData) return null;

    const chartData: Array<BarChartDataPoint<string>> = statsData.map(stat => {
        return {
            label: stat.yearMonth.toLabel(),
            data: {
                "x": stat.investmentRevenue,
            }
        };
    });

    return (
        <BarChart data={chartData} labels={{"x": "Label"}} title="Investment Revenue"/>
    );
};

export default InvestmentRevenueChart;
