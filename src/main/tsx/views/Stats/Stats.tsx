import React from "react";

import Select from "@/components/Select/Select";
import useGetQuery from "@/hooks/useGetQuery";
import usePersistentState from "@/hooks/usePersistentState";
import Stats, {StatsDTO, statsMapper} from "@/types/Stats";
import StatsChart from "@/views/Stats/Chart/StatsChart";
import StatsTable from "@/views/Stats/Table/StatsTable";

export type StatsMode = { processing: 'raw' | 'smoothed', shared: boolean };

const Stats: React.FC = () => {
    const {data: stats, error, isLoading} = useGetQuery<StatsDTO, Stats>('stats', statsMapper.fromDTO);

    const [mode, setMode] = usePersistentState<StatsMode>('statsMode', {processing: 'smoothed', shared: false});

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!stats) return <div>No data available</div>;

    const statsModeOptions: Array<{ key: StatsMode, label: string }> = [
        {key: {processing: 'raw', shared: false}, label: 'Normal'},
        {key: {processing: 'smoothed', shared: false}, label: 'Smoothed (monthly)'},
        {key: {processing: 'smoothed', shared: true}, label: 'Smoothed (shared)'},
    ];

    return <div style={{padding: 20}}>
        <Select
            options={statsModeOptions}
            initialKey={mode}
            onSelect={(key: StatsMode) => setMode(key)}
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
    </div>;
};

export default Stats;
