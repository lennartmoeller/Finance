import React, {useMemo, useState} from "react";

import {
    ActiveElement,
    BarController,
    BarElement,
    CategoryScale,
    ChartData,
    ChartEvent,
    Chart as ChartJS,
    ChartOptions,
    Filler,
    LinearScale,
    LineElement,
    Plugin,
    PointElement,
} from "chart.js";
import {Bar} from "react-chartjs-2";
import {merge} from "ts-deepmerge";

import {getEuroString} from "@/utils/money";

export interface BarChartDataPoint<T extends string | number | symbol> {
    label: string;
    data: Record<T, number>;
}

ChartJS.register(BarController, BarElement, CategoryScale, Filler, LineElement, LinearScale, PointElement);

const getChartData = <T extends string | number | symbol, >(data: Array<BarChartDataPoint<T>>, labels: Record<T, string>): ChartData<"bar"> => ({
    datasets: Object.entries(labels).map(([key, label])=> ({
        label: label as string,
        data: data.map((point) => point.data[key as T]),
    })),
    labels: data.map((point) => point.label),
});

const getChartOptions = (custom: ChartOptions<"bar">, onHoverCallback?: (hoverIndex: number | null) => void): ChartOptions<"bar"> => {
    const chartOptions: ChartOptions<"bar"> = {
        responsive: true,
        animation: false,
        events: ["mousemove", "mouseout"],
        interaction: {
            mode: "index",
            intersect: false,
        },
        scales: {
            x: {
                display: false,
                stacked: true,
            },
            y: {
                display: true,
                stacked: true,
                grid: {
                    display: true,
                    color: "#eee",
                },
                border: {
                    display: false,
                },
                position: "center",
                ticks: {
                    display: false,
                    // stepSize: gridStepSize, TODO
                },
                // beginAtZero: true, TODO
                // min: yMin, TODO
                // max: yMax, TODO
            },
        },
        onHover: (_event: ChartEvent, chartElement: ActiveElement[]) => {
            if (!onHoverCallback) return;
            if (chartElement.length > 0) {
                onHoverCallback(chartElement[0].index);
            } else {
                onHoverCallback(null);
            }
        },
    };

    return merge(chartOptions, custom);
};

const getChartPlugins = (setHoveredIndex: (hoverIndex: number | null) => void): Plugin<"bar">[] => {
    return [
        {
            id: 'resetHoveredIndex',
            beforeEvent: (_chart, args) => {
                args.event.type === 'mouseout' && setHoveredIndex(null);
            },
        }
    ];
};

interface BarChartProps<T extends string | number | symbol> {
    data: Array<BarChartDataPoint<T>>;
    labels: Record<T, string>;
    options?: ChartOptions<"bar">;
    title: string;
}

const BarChart = <T extends string | number | symbol>({data, labels, options, title}: BarChartProps<T>) => {
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    const chartData = useMemo(() => getChartData(data, labels), [data, labels]);
    const chartOptions = useMemo(() => getChartOptions(options ?? {}, setHoveredIndex), [options]);
    const chartPlugins = getChartPlugins(setHoveredIndex);

    return (
        <div style={{width: "calc(100% + 1px)", marginRight: "-1px", marginBottom: "-1px"}}>
            {(() => {
                const point: BarChartDataPoint<T> = data[hoveredIndex ?? data.length - 1];
                return (<div style={{
                    height: "70px",
                    display: "grid",
                    gridTemplateColumns: "1fr min-content",
                    alignItems: "center",
                    padding: "0 18px"
                }}>
                    <div>
                        <div style={{
                            fontSize: "18px",
                            fontWeight: 600,
                            marginBottom: "2px",
                        }}>{title}</div>
                        <div style={{fontSize: "13px"}}>{point.label}</div>
                    </div>
                    <div style={{display: "grid", gridAutoFlow: "column", gridAutoColumns: "100px", gap: "10px"}}>
                        <div>
                            <div style={{
                                fontSize: "12px",
                                textTransform: "uppercase",
                                fontWeight: 500,
                                marginBottom: "2px",
                                textAlign: "center",
                            }}>Value
                            </div>
                            <div style={{
                                fontSize: "16px",
                                textAlign: "center",
                            }}>{getEuroString(0)}</div>
                        </div>
                        <div>
                            <div style={{
                                fontSize: "12px",
                                textTransform: "uppercase",
                                fontWeight: 500,
                                marginBottom: "2px",
                                textAlign: "center",
                            }}>Target
                            </div>
                            <div style={{
                                fontSize: "16px",
                                textAlign: "center",
                            }}>{getEuroString(0)}</div>
                        </div>
                        <div>
                            <div style={{
                                fontSize: "12px",
                                textTransform: "uppercase",
                                fontWeight: 500,
                                marginBottom: "2px",
                                textAlign: "center",
                            }}>Diff
                            </div>
                            <div style={{
                                fontSize: "16px",
                                textAlign: "center",
                            }}>{getEuroString(0)}</div>
                        </div>
                    </div>
                </div>);
            })()}

            <Bar
                data={chartData}
                options={chartOptions}
                plugins={chartPlugins}
            />
        </div>
    );
};

export default BarChart;
