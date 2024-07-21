/*
import {useGetQuery} from "@/mapper/useGetQuery";
import {Stats} from "@/types/Stats";
import {StatsDTO} from "@/types/StatsDTO";
import React from "react";

const Stats: React.FC = () => {
    const {data, error, isLoading} = useGetQuery<Stats, StatsDTO>('stats');

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!data) return <div>No data available</div>;

    return (
        <></>
    );
};

export default Stats;
*/