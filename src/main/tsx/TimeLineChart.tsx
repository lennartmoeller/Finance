import React, { useRef, useEffect } from 'react';
import Chart from 'chart.js/auto';

interface TimeLineChartProps {
    data: Array<{ [key: string]: any }>;
    xProperty: string;
    yProperties: Array<string>;
    alwaysShowXBar?: boolean;
}

export const TimeLineChart: React.FC<TimeLineChartProps> = ({ data, xProperty, yProperties, alwaysShowXBar = false }) => {
    const chartRef = useRef<HTMLCanvasElement | null>(null);
    const chartInstanceRef = useRef<Chart | null>(null);

    useEffect(() => {
        if (chartRef.current) {
            const ctx = chartRef.current.getContext('2d');

            if (ctx) {
                // Group data by month and year
                const groupedData = data.reduce((acc, item, index) => {
                    const date = new Date(item[xProperty]);
                    const monthYear = `${date.getMonth() + 1}-${date.getFullYear()}`;
                    if (!acc[monthYear]) {
                        acc[monthYear] = [];
                    }
                    acc[monthYear].push(index);
                    return acc;
                }, {} as Record<string, number[]>);

                // Create labels at the midpoint of each month
                const labels = data.map((item, index) => {
                    const date = new Date(item[xProperty]);
                    const monthYear = `${date.getMonth() + 1}-${date.getFullYear()}`;
                    if (groupedData[monthYear] && groupedData[monthYear].includes(index)) {
                        const middleIndex = groupedData[monthYear][Math.floor(groupedData[monthYear].length / 2)];
                        if (middleIndex === index) {
                            return `${date.toLocaleString('default', { month: 'short' })} ${date.getFullYear()}`;
                        }
                    }
                    return '';
                });

                const datasets = yProperties.map((yProperty, idx) => {
                    const colors = [
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        // Add more colors if needed
                    ];
                    const backgroundColors = [
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        // Add more colors if needed
                    ];
                    return {
                        label: yProperty.charAt(0).toUpperCase() + yProperty.slice(1),
                        data: data.map(item => item[yProperty]),
                        borderColor: colors[idx % colors.length],
                        backgroundColor: backgroundColors[idx % backgroundColors.length],
                        fill: true,
                    };
                });

                chartInstanceRef.current = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: datasets
                    },
                    options: {
                        responsive: true,
                        scales: {
                            x: {
                                title: {
                                    display: true,
                                    text: 'Date',
                                },
                                ticks: {
                                    autoSkip: false, // Ensure all labels are considered
                                    callback: function(value, index, ticks) {
                                        return labels[index];
                                    }
                                },
                                grid: {
                                    display: false, // Hide vertical grid lines
                                }
                            },
                            y: {
                                title: {
                                    display: true,
                                    text: 'Value',
                                },
                                // Ensuring x-axis is always shown
                                min: alwaysShowXBar ? Math.min(0, Math.min(...data.flatMap(item => yProperties.map(prop => item[prop])))) : undefined
                            }
                        }
                    }
                });
            }
        }

        // Cleanup function to destroy the chart when the component unmounts
        return () => {
            if (chartInstanceRef.current) {
                chartInstanceRef.current.destroy();
            }
        };
    }, [data, xProperty, yProperties, alwaysShowXBar]);

    return (
        <div>
            <canvas ref={chartRef} />
        </div>
    );
}
