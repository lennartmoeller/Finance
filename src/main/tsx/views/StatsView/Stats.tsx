import React, {useState} from "react";

import Select from "@/components/Select/Select";
import {useStats} from "@/services/stats";
import StatsChart from "@/views/StatsView/Chart/StatsChart";
import StatsTable from "@/views/StatsView/Table/StatsTable";

export type StatsMode = { processing: 'raw' | 'smoothed', shared: boolean };

const Stats: React.FC = () => {
    const stats = useStats();

    const [mode, setMode] = useState<StatsMode>({processing: 'smoothed', shared: false});

    if (stats.isLoading) return <div>Loading...</div>;
    if (stats.error) return <div>Error: {stats.error.message}</div>;
    if (!stats.data) return <div>No data available</div>;

    const statsModeOptions: Array<{ key: StatsMode, label: string }> = [
        {key: {processing: 'raw', shared: false}, label: 'Normal'},
        {key: {processing: 'smoothed', shared: false}, label: 'Smoothed (monthly)'},
        {key: {processing: 'smoothed', shared: true}, label: 'Smoothed (shared)'},
    ];

    return <div style={{padding: 20, width: "max-content"}}>
        <Select
            options={statsModeOptions}
            initialKey={mode}
            onSelect={(key: StatsMode) => setMode(key)}
        />
        <div style={{marginLeft: '341.5px'}}>
            <StatsChart
                mode={mode}
                stats={stats.data.dailyStats}
            />
        </div>
        <StatsTable
            mode={mode}
            stats={stats.data}
        />
    </div>;
};

export default Stats;
