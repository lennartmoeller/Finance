import React, { useMemo, useRef, useState } from "react";

import {
    ActiveElement,
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
    ScriptableContext,
} from "chart.js";
import { Line } from "react-chartjs-2";
import { merge } from "ts-deepmerge";

import useElementWidth from "@/hooks/useElementWidth";
import { getEuroString } from "@/utils/money";

export interface LineChartDataPoint {
    label: string;
    value: number;
    target: number;
}

ChartJS.register(CategoryScale, Filler, LineElement, LinearScale, PointElement);

const getDecimatedData = (
    data: Array<LineChartDataPoint>,
    chartWidth: number,
): Array<LineChartDataPoint> => {
    const samples: number = Math.round(chartWidth / 2);

    if (data.length <= samples || data.length <= 2 || samples <= 2) {
        return data; // No decimation needed
    }

    const result: Array<LineChartDataPoint> = [];

    const totalDataPoints: number = data.length;
    const step: number = (totalDataPoints - 2) / (samples - 2);

    // first data point as it is
    result.push(data[0]);

    for (let i: number = 0; i < samples - 2; i++) {
        const startIndexDec: number = i * step + 1;
        const endIndexDec: number = (i + 1) * step + 1;

        const startIdx: number = Math.floor(startIndexDec);
        const endIdx: number = Math.floor(endIndexDec);

        const startWeight: number = 1 - (startIndexDec - startIdx);
        const endWeight: number = endIndexDec - endIdx;

        let totalValue: number = 0;
        let totalTarget: number = 0;
        let totalWeight: number = 0;

        for (let j: number = startIdx; j <= endIdx; j++) {
            const weight: number =
                j === startIdx ? startWeight : j === endIdx ? endWeight : 1;
            totalValue += data[j].value * weight;
            totalTarget += data[j].target * weight;
            totalWeight += weight;
        }

        const avgValue: number = totalValue / totalWeight;
        const avgTarget: number = totalTarget / totalWeight;

        const labelIndex: number = Math.round(
            (startIndexDec + endIndexDec) / 2,
        );

        result.push({
            label: data[labelIndex].label,
            value: avgValue,
            target: avgTarget,
        });
    }

    // last datapoint as it is
    result.push(data[totalDataPoints - 1]);

    return result;
};

function getGridStepSize(min: number, max: number): number {
    const range = max - min;
    const target = range / 5;
    const exponent = Math.floor(Math.log10(target));
    for (let e = exponent; e > -100; e--) {
        const base = 10 ** e;
        let best = 0;
        for (const factor of [1, 2, 5, 10]) {
            const step = factor * base;
            if (step < target && step > best) best = step;
        }
        if (best > 0) return best;
    }
    return 0;
}

const getChartData = (
    decimatedData: Array<LineChartDataPoint>,
    onlyPositive: boolean,
): ChartData<"line"> => ({
    datasets: [
        {
            label: "Value",
            borderWidth: 2.5,
            fill: true,
            backgroundColor: (scriptableContext: ScriptableContext<"line">) => {
                if (!onlyPositive) {
                    return "rgba(0, 0, 0, 0)";
                }
                const ctx: CanvasRenderingContext2D =
                    scriptableContext.chart.ctx;
                const height: number = scriptableContext.chart.height;
                const gradient: CanvasGradient = ctx.createLinearGradient(
                    0,
                    0,
                    0,
                    height,
                );
                gradient.addColorStop(0, "rgba(76, 175, 80, 0.4)");
                gradient.addColorStop(1, "rgba(76, 175, 80, 0)");
                return gradient;
            },
            data: decimatedData.map((point) => point.value),
        },
        {
            label: "Target",
            borderWidth: 2.5,
            fill: {
                target: "0",
                above: "rgba(255, 0, 0, 0.3)",
                below: `rgba(76, 175, 80, ${onlyPositive ? 0 : 0.3})`,
            },
            data: decimatedData.map((point) => point.target),
        },
    ],
    labels: decimatedData.map((point) => point.label),
});

const getChartOptions = (
    yMin: number,
    yMax: number,
    gridStepSize: number,
    custom: ChartOptions<"line">,
    onHoverCallback?: (hoverIndex: number | null) => void,
): ChartOptions<"line"> => {
    const chartOptions: ChartOptions<"line"> = {
        responsive: true,
        animation: false,
        events: ["mousemove", "mouseout"],
        layout: {
            autoPadding: false,
        },
        plugins: {
            filler: {
                drawTime: "beforeDatasetsDraw",
            },
        },
        interaction: {
            mode: "index",
            intersect: false,
        },
        scales: {
            x: {
                display: false,
            },
            y: {
                display: true,
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
                    stepSize: gridStepSize,
                },
                beginAtZero: true,
                min: yMin,
                max: yMax,
            },
        },
        elements: {
            point: {
                radius: 0,
                hoverRadius: 5,
                hoverBorderWidth: 3,
                hoverBackgroundColor: "white",
                hoverBorderColor: "rgb(122,122,122)",
            },
        },
        borderColor: "#777",
        hover: {
            mode: "index",
        },
        transitions: {
            active: { animation: { duration: 0 } },
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

const getChartPlugins = (
    setHoveredIndex: (hoverIndex: number | null) => void,
): Plugin<"line">[] => {
    return [
        {
            id: "resetHoveredIndex",
            beforeEvent: (_chart, args) => {
                if (args.event.type === "mouseout") {
                    setHoveredIndex(null);
                }
            },
        },
    ];
};

const getDimensions = (
    data: Array<LineChartDataPoint>,
): { yMin: number; yMax: number; gridStepSize: number } => {
    const { yMinDp, yMaxDp } = data.reduce(
        (acc, point) => ({
            yMinDp: Math.min(acc.yMinDp, point.value, point.target),
            yMaxDp: Math.max(acc.yMaxDp, point.value, point.target),
        }),
        { yMinDp: Infinity, yMaxDp: -Infinity },
    );

    let yMin: number = yMinDp;
    let yMax: number = yMaxDp;

    if (yMin > 0) {
        yMin = 0;
    }
    if (yMax < 0) {
        yMax = 0;
    }

    const gridStepSize = getGridStepSize(yMin, yMax);
    yMin = Math.floor(yMin / gridStepSize) * gridStepSize;
    yMax = Math.ceil(yMax / gridStepSize) * gridStepSize;

    if (yMin === yMinDp) yMin -= gridStepSize;
    if (yMax === yMaxDp) yMax += gridStepSize;

    return { yMin, yMax, gridStepSize };
};

interface LineChartProps {
    data: LineChartDataPoint[];
    options?: ChartOptions<"line">;
    title: string;
}

const LineChart: React.FC<LineChartProps> = ({ data, options, title }) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const chartWidth: number = useElementWidth(chartContainerRef);

    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    const { yMin, yMax, gridStepSize, onlyPositive, targetAlwaysZero } =
        useMemo(() => {
            const dimensions = getDimensions(data);
            return {
                ...dimensions,
                onlyPositive: dimensions.yMin >= 0,
                targetAlwaysZero: data.every(
                    (dp: LineChartDataPoint) => dp.target === 0,
                ),
            };
        }, [data]);

    const decimatedData = useMemo(
        () => getDecimatedData(data, chartWidth),
        [data, chartWidth],
    );
    const chartData = useMemo(
        () => getChartData(decimatedData, onlyPositive),
        [decimatedData, onlyPositive],
    );
    const chartOptions = useMemo(
        () =>
            getChartOptions(
                yMin,
                yMax,
                gridStepSize,
                options ?? {},
                setHoveredIndex,
            ),
        [yMin, yMax, gridStepSize, options],
    );
    const chartPlugins = getChartPlugins(setHoveredIndex);

    return (
        <div
            ref={chartContainerRef}
            style={{
                width: "calc(100% + 1px)",
                marginRight: "-1px",
                marginBottom: "-1px",
            }}
        >
            {(() => {
                const point: LineChartDataPoint =
                    decimatedData[hoveredIndex ?? decimatedData.length - 1];
                return (
                    <div
                        style={{
                            height: "70px",
                            display: "grid",
                            gridTemplateColumns: "1fr min-content",
                            alignItems: "center",
                            padding: "0 18px",
                        }}
                    >
                        <div>
                            <div
                                style={{
                                    fontSize: "18px",
                                    fontWeight: 600,
                                    marginBottom: "2px",
                                }}
                            >
                                {title}
                            </div>
                            <div style={{ fontSize: "13px" }}>
                                {point.label}
                            </div>
                        </div>
                        <div
                            style={{
                                display: "grid",
                                gridAutoFlow: "column",
                                gridAutoColumns: "100px",
                                gap: "10px",
                            }}
                        >
                            <div>
                                <div
                                    style={{
                                        fontSize: "12px",
                                        textTransform: "uppercase",
                                        fontWeight: 500,
                                        marginBottom: "2px",
                                        textAlign: "center",
                                    }}
                                >
                                    Value
                                </div>
                                <div
                                    style={{
                                        fontSize: "16px",
                                        textAlign: "center",
                                    }}
                                >
                                    {getEuroString(point.value)}
                                </div>
                            </div>
                            {!targetAlwaysZero && (
                                <>
                                    <div>
                                        <div
                                            style={{
                                                fontSize: "12px",
                                                textTransform: "uppercase",
                                                fontWeight: 500,
                                                marginBottom: "2px",
                                                textAlign: "center",
                                            }}
                                        >
                                            Target
                                        </div>
                                        <div
                                            style={{
                                                fontSize: "16px",
                                                textAlign: "center",
                                            }}
                                        >
                                            {getEuroString(point.target)}
                                        </div>
                                    </div>
                                    <div>
                                        <div
                                            style={{
                                                fontSize: "12px",
                                                textTransform: "uppercase",
                                                fontWeight: 500,
                                                marginBottom: "2px",
                                                textAlign: "center",
                                            }}
                                        >
                                            Diff
                                        </div>
                                        <div
                                            style={{
                                                fontSize: "16px",
                                                textAlign: "center",
                                            }}
                                        >
                                            {getEuroString(
                                                point.value - point.target,
                                            )}
                                        </div>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                );
            })()}

            <Line
                key={chartWidth}
                data={chartData}
                options={chartOptions}
                plugins={chartPlugins}
            />
        </div>
    );
};

export default LineChart;
