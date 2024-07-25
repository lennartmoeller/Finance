import React from "react";
import {useGetQuery} from "@/hooks/useGetQuery";
import usePersistentState from "@/hooks/usePersistentState";
import {Stats, StatsDTO, statsMapper} from "@/types/Stats";
import StatsTable from "@/views/Stats/Table/StatsTable";
import StatsChart from "@/views/Stats/Chart/StatsChart";
import Select from "@/components/Select";

export type StatsMode = 'surplus' | 'smoothedSurplus';

const Stats: React.FC = () => {
    const {data: stats, error, isLoading} = useGetQuery<StatsDTO, Stats>('stats', statsMapper.fromDTO);

    const [mode, setMode] = usePersistentState<StatsMode>('statsMode', 'smoothedSurplus');

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!stats) return <div>No data available</div>;

    const statsModeOptions: Array<{ key: string, label: string }> = [
        {key: 'surplus', label: 'Normal'},
        {key: 'smoothedSurplus', label: 'Smoothed'}
    ];

    return <>
        <Select
            options={statsModeOptions}
            initialKey={mode}
            onSelect={(key: string) => setMode(key as StatsMode)}
        />
        <div style={{marginLeft: '220px'}}>
            <StatsChart
                mode={mode}
                stats={stats}
            />
        </div>
        <StatsTable
            stats={stats}
            mode={mode}
        />
    </>;
};

export default Stats;
