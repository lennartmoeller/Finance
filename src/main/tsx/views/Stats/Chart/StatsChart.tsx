import React, {useEffect, useRef, useState} from 'react';
import {createChart, IChartApi, ISeriesApi, UTCTimestamp} from 'lightweight-charts';
import styled from 'styled-components';
import {Stats} from '@/types/Stats';
import {StatsMode} from '@/views/Stats/Stats';
import Tooltip from "@/components/Tooltip/Tooltip";

interface ChartWrapperProps {
    mode: StatsMode;
    stats: Stats;
}

interface HoverData {
    position: { x: number; y: number };
    time: UTCTimestamp;
    price: number;
}

const ChartContainer = styled.div<{ width: number; height: number }>`
    width: ${(props) => props.width}px;
    height: ${(props) => props.height}px;
    overflow: hidden;
`;

const ChartWrapper: React.FC<ChartWrapperProps> = ({mode, stats}) => {
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const chartRef = useRef<IChartApi | null>(null);
    const seriesRef = useRef<ISeriesApi<'Area'> | null>(null);

    const width = stats.dailyStats.length * 4 + stats.monthlyStats.length;
    const height = 400;

    const [hoverData, setHoverData] = useState<HoverData | null>(null);

    useEffect(() => {
        if (!chartContainerRef.current) return;

        const chartData = stats.dailyStats.map((datapoint) => ({
            time: (datapoint.date.getTime() / 1000) as UTCTimestamp,
            value: datapoint[mode === 'rawSurplus' ? 'balance' : 'smoothedBalance'],
        }));

        if (chartRef.current) {
            chartRef.current.remove();
        }

        chartRef.current = createChart(chartContainerRef.current, {
            width,
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

        seriesRef.current = chartRef.current.addAreaSeries({
            lastValueVisible: false,
            priceLineVisible: false,
            topColor: 'rgba(76, 175, 80, 0.4)',
            bottomColor: 'rgba(76, 175, 80, 0.1)',
            lineColor: 'rgba(76, 175, 80, 1)',
        });

        seriesRef.current.setData(chartData);
        chartRef.current.timeScale().fitContent();

        chartRef.current.subscribeCrosshairMove((param) => {
            if (seriesRef.current && param.point && param.seriesData.size > 0) {
                const seriesData = param.seriesData.get(seriesRef.current);
                if (seriesData) {
                    const {value, time} = seriesData as { time: UTCTimestamp; value: number };
                    const priceCoordinate = seriesRef.current.priceToCoordinate(value);
                    if (priceCoordinate) {
                        const rect = chartContainerRef.current?.getBoundingClientRect();
                        if (rect) {
                            setHoverData({
                                position: {
                                    x: param.point.x + rect.left,
                                    y: priceCoordinate + rect.top,
                                },
                                time,
                                price: value,
                            });
                            return; // success
                        }
                    }
                }
            }
            setHoverData(null);
        });

        return () => {
            chartRef.current?.remove();
            chartRef.current = null;
        };
    }, [mode, stats, width]);

    return (
        <>
            <ChartContainer ref={chartContainerRef} width={width} height={height}/>
            {hoverData &&
                <Tooltip {...hoverData.position}>
                    <div>
                        {(new Date(hoverData.time * 1000)).toLocaleDateString('de-de', {
                            year: "numeric",
                            month: "short",
                            day: "numeric"
                        })}
                    </div>
                    <div style={{fontSize: "14px", fontWeight: "bold"}}>
                        {new Intl.NumberFormat('de-DE', {
                            style: 'currency',
                            currency: 'EUR'
                        }).format(hoverData.price / 100)}
                    </div>
                </Tooltip>}
        </>
    );
};

export default ChartWrapper;
