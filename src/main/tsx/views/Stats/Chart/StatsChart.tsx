import React, {useEffect, useRef} from 'react';

import {createChart, IChartApi, ISeriesApi, UTCTimestamp} from 'lightweight-charts';
import styled from 'styled-components';

import {Stats} from '@/types/Stats';
import {StatsMode} from '@/views/Stats/Stats';

interface ChartWrapperProps {
    mode: StatsMode;
    stats: Stats;
}

const ChartContainer = styled.div<{ width: number; height: number }>`
    width: ${(props) => props.width}px;
    height: ${(props) => props.height}px;
    overflow: hidden;
`;

const ChartWrapper: React.FC<ChartWrapperProps> = ({mode, stats}) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const chartRef = useRef<IChartApi | null>(null);
    const seriesRef = useRef<ISeriesApi<'Line'> | null>(null);

    const width = stats.dailyStats.length * 4;
    const height = 400;

    useEffect(() => {
        if (!chartContainerRef.current) return;

        const chartData = stats.dailyStats.map((datapoint) => ({
            time: (datapoint.date.getTime() / 1000) as UTCTimestamp,
            value: datapoint[mode === 'surplus' ? 'balance' : 'smoothedBalance'],
        }));

        if (chartRef.current) {
            chartRef.current.remove();
        }

        chartRef.current = createChart(chartContainerRef.current, {
            width: width,
            height: height + 28, // to hide the x-axis
            handleScale: false,
            handleScroll: false,
            layout: {
                textColor: 'transparent',
            },
            timeScale: {
                borderVisible: false,
            },
            rightPriceScale: {
                borderVisible: false,
                visible: false,
            },
            leftPriceScale: {
                visible: false,
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
            crosshair: {
                horzLine: {
                    visible: false,
                    labelVisible: false,
                },
                vertLine: {
                    visible: false,
                    labelVisible: false,
                },
            },
        });

        seriesRef.current = chartRef.current.addLineSeries({
            lastValueVisible: false,
            priceLineVisible: false,
        });

        seriesRef.current.setData(chartData);
        chartRef.current.timeScale().fitContent();

        return () => {
            chartRef.current?.remove();
            chartRef.current = null;
        };
    }, [mode, stats, width, height]);

    return (
        <ChartContainer
            ref={chartContainerRef}
            width={width}
            height={height}
        />
    );
};

export default ChartWrapper;
