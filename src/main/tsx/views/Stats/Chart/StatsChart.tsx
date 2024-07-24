import React, { useEffect, useRef, useState } from 'react';

import { createChart, IChartApi, ISeriesApi, LineData, SeriesMarker, SeriesMarkerShape, Time, UTCTimestamp } from 'lightweight-charts';

import {Stats} from "@/types/Stats";
import {StatsMode} from "@/views/Stats/Stats";

interface ChartWrapperProps {
    mode: StatsMode,
    stats: Stats;
}

const ChartWrapper: React.FC<ChartWrapperProps> = ({mode, stats}) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const chartRef = useRef<IChartApi | null>(null);
    const seriesRef = useRef<ISeriesApi<'Line'> | null>(null);
    const [containerWidth, setContainerWidth] = useState(0);
    
    const data = stats.dailyStats.map(datapoint => ({
        time: datapoint.date,
        value: datapoint[mode === 'surplus' ? 'balance' : 'smoothedBalance']
    }));

    const height: number = 400;

    useEffect(() => {
        const transformedData: LineData[] = data.map(point => ({
            time: (point.time.getTime() / 1000) as UTCTimestamp, // Convert Date to UNIX timestamp and cast to UTCTimestamp
            value: point.value
        }));

        const markers: SeriesMarker<Time>[] = [];
        const seenMonths = new Set<string>();

        transformedData.forEach(point => {
            const date = new Date((point.time as number) * 1000); // Cast point.time as number
            const monthKey = `${date.getUTCFullYear()}-${date.getUTCMonth()}`;
            if (!seenMonths.has(monthKey)) {
                seenMonths.add(monthKey);
                markers.push({
                    time: point.time,
                    position: 'belowBar',
                    color: 'rgba(197, 203, 206, 0.5)',
                    shape: 'circle' as SeriesMarkerShape,
                    text: ''
                });
            }
        });

        if (chartContainerRef.current) {
            chartRef.current = createChart(chartContainerRef.current, {
                width: containerWidth,
                height,
                handleScroll: false,   // Disable scrolling
                handleScale: false     // Disable zooming
            });

            chartRef.current.applyOptions({
                layout: {
                    textColor: 'transparent',  // Make text color transparent
                },
                timeScale: {
                    borderVisible: false,
                    timeVisible: true,
                    secondsVisible: true,

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

            seriesRef.current = chartRef.current.addLineSeries();
            seriesRef.current.setData(transformedData);

            // Apply markers for each month's start
            seriesRef.current.setMarkers(markers);

            chartRef.current.timeScale().fitContent(); // Fit all data points within the view

            const handleResize = () => {
                if (chartContainerRef.current) {
                    setContainerWidth(chartContainerRef.current.clientWidth);
                }
            };

            window.addEventListener('resize', handleResize);
            handleResize(); // Initial call to set the width

            return () => {
                if (chartRef.current) {
                    chartRef.current.remove();
                }
                window.removeEventListener('resize', handleResize);
            };
        }
    }, [data, height, containerWidth]);

    useEffect(() => {
        if (chartRef.current) {
            chartRef.current.resize(containerWidth, height);
            chartRef.current.timeScale().fitContent(); // Adjust view to fit all data points after resizing
        }
    }, [containerWidth, height]);

    return <div ref={chartContainerRef} style={{ width: '100%', height }} />;
};

export default ChartWrapper;
