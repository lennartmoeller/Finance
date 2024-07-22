import React from "react";

import {useGetQuery} from "@/api/useGetQuery";
import {Stats, StatsDTO, statsMapper} from "@/types/Stats";

const Stats: React.FC = () => {
    const {data, error, isLoading} = useGetQuery<StatsDTO, Stats>('stats', statsMapper.fromDTO);

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!data) return <div>No data available</div>;

    console.log(data);

    return (
        <div>DONE</div>
    );

};

export default Stats;
