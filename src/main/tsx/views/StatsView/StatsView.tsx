import React, {useEffect} from "react";

import Icon from "@/components/Icon/Icon";
import Switch from "@/components/Switch/Switch";
import {useStats} from "@/services/stats";
import useHeader from "@/skeleton/Header/stores/useHeader";
import useStatsMode from "@/views/StatsView/stores/useStatsMode";
import StatsTable from "@/views/StatsView/Table/StatsTable";

const StatsView: React.FC = () => {
    const {setHeader} = useHeader();
    const {smoothed, setSmoothed, merged, setMerged,} = useStatsMode();
    const stats = useStats();

    useEffect(() => {
        setHeader({
            actions: (
                <>
                    {smoothed && (
                        <Switch
                            content={(checked) => (
                                <Icon id="fa-solid fa-merge"
                                      opacity={checked ? 1 : 0.5}
                                      size={30}
                                />
                            )}
                            initial={merged}
                            onChange={setMerged}
                        />
                    )}
                    <Switch
                        content={(checked) => (
                            <Icon id="fa-solid fa-blender"
                                  opacity={checked ? 1 : 0.5}
                                  size={30}
                            />
                        )}
                        initial={smoothed}
                        onChange={setSmoothed}
                    />
                </>
            ),
        });
    }, [merged, setHeader, setMerged, setSmoothed, smoothed]);

    if (stats.isLoading) return <div>Loading...</div>;
    if (stats.error) return <div>Error: {stats.error.message}</div>;
    if (!stats.data) return <div>No data available</div>;

    return (
        <StatsTable
            stats={stats.data}
            merged={merged}
            smoothed={smoothed}
        />
    );
};

export default StatsView;
