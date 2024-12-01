import React, {useMemo} from "react";

import {
    CategoryScale,
    Chart as ChartJS,
    ChartOptions,
    Filler,
    LinearScale,
    LineElement,
    PointElement,
    ScriptableContext,
    Tooltip,
    TooltipItem,
} from 'chart.js';
import {Line} from 'react-chartjs-2';

import {useDailyBalanceStats} from "@/services/dailyBalanceStats";
import {getEuroString} from "@/utils/money";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Filler);

const chartOptions: ChartOptions<'line'> = {
    responsive: true,
    layout: {
        autoPadding: false,
    },
    plugins: {
        tooltip: {
            enabled: true,
            displayColors: false,
            position: 'nearest',
            padding: 8,
            caretPadding: 12,
            yAlign: 'top',
            bodyAlign: 'center',
            titleAlign: 'center',
            callbacks: {
                label: (tooltipItem: TooltipItem<'line'>) => `${getEuroString(tooltipItem.raw as number)}`,
            },
        },
    },
    interaction: {
        mode: 'index',
        intersect: false,
    },
    scales: {
        x: {
            display: false,
        },
        y: {
            display: true,
            min: 0,
            grid: {
                display: true,
                color: 'rgba(200, 200, 200, 0.5)',
            },
            border: {
                display: false,
            },
            position: "center",
            ticks: {
                display: false,
                stepSize: 250000,
            },
        },
    },
    elements: {
        point: {
            radius: 0,
            hoverRadius: 5,
            hoverBorderWidth: 3,
            hoverBorderColor: 'rgba(76, 175, 80, 1)',
            hoverBackgroundColor: 'white',
        },
    },
    hover: {mode: 'index'},
    transitions: {active: {animation: {duration: 0}}},
};

const DiagramView: React.FC = () => {
    const {data: statsData} = useDailyBalanceStats();

    const chartData = useMemo(() => {
        if (!statsData) return null;

        const labels: string[] = statsData.map(stat =>
            new Date(stat.date).toLocaleDateString('de-DE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            })
        );

        const datasets = [{
            label: 'Smoothed Value',
            data: statsData.map(stat => stat.balance.smoothed),
            fill: true,
            borderColor: 'rgba(76, 175, 80, 1)',
            backgroundColor: ({chart: {ctx, height}}: ScriptableContext<'line'>) => {
                const gradientFill = ctx.createLinearGradient(0, 0, 0, height);
                gradientFill.addColorStop(0, "rgba(76, 175, 80, 0.4)");
                gradientFill.addColorStop(1, "rgba(76, 175, 80, 0)");
                return gradientFill;
            },
            tension: 0.5,
            borderWidth: 3.5,
        }];

        return {labels, datasets};
    }, [statsData]);

    return chartData ? (
        <div style={{margin: "-1px"}}>
            <Line options={chartOptions} data={chartData}/>
        </div>
    ) : <p>No data available</p>;
};

export default DiagramView;
