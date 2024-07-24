import React, { useEffect, useRef, useState } from 'react';
import { createChart, IChartApi, ISeriesApi, LineData, Time, UTCTimestamp } from 'lightweight-charts';
import { Stats } from "@/types/Stats";
import { StatsMode } from "@/views/Stats/Stats";

interface ChartWrapperProps {
    mode: StatsMode;
    stats: Stats;
}

const ChartWrapper: React.FC<ChartWrapperProps> = ({ mode, stats }) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const chartRef = useRef<IChartApi | null>(null);
    const seriesRef = useRef<ISeriesApi<'Line'> | null>(null);

    const data = stats.dailyStats.map(datapoint => ({
        time: datapoint.date,
        value: datapoint[mode === 'surplus' ? 'balance' : 'smoothedBalance']
    }));

    const width: number = stats.dailyStats.length * 4;
    const height: number = 400;

    useEffect(() => {
        const transformedData: LineData[] = data.map(point => ({
            time: (point.time.getTime() / 1000) as UTCTimestamp, // Convert Date to UNIX timestamp and cast to UTCTimestamp
            value: point.value
        }));

        if (chartContainerRef.current) {
            // Cleanup existing chart if it exists
            if (chartRef.current) {
                chartRef.current.remove();
            }

            chartRef.current = createChart(chartContainerRef.current, {
                width,
                height,
                handleScale: false, // disable zooming
                handleScroll: false, // disable scrolling
                layout: {
                    textColor: 'transparent', // Make text color transparent
                },
                timeScale: {
                    borderVisible: false,
                    barSpacing: 4 // Ensure days have a distance of 4px
                },
                rightPriceScale: {
                    borderVisible: false,
                    visible: false, // Hide price scale
                },
                leftPriceScale: {
                    visible: false, // Hide left price scale if enabled
                },
                grid: {
                    vertLines: {
                        color: 'rgba(197, 203, 206, 0.5)',
                        style: 1,
                        visible: true,
                    },
                    horzLines: {
                        visible: false,
                    },
                },
            });

            seriesRef.current = chartRef.current.addLineSeries({
                lastValueVisible: false, // Hide the horizontal line for the last value
                priceLineVisible: false, // Hide the price line for the last value
            });
            seriesRef.current.setData(transformedData);

            chartRef.current.timeScale().fitContent(); // Fit all data points within the view
        }

        // Cleanup function to remove the chart
        return () => {
            if (chartRef.current) {
                chartRef.current.remove();
                chartRef.current = null;
            }
        };
    }, [data, width]);

    return <div ref={chartContainerRef} />;
};

export default ChartWrapper;
