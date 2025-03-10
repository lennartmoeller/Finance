import React from "react";

import {useMonthlySavingStats} from "@/services/monthlySavingStats";
import {getEuroString} from "@/utils/money";
import Goal from "@/views/DashboardView/Goal/Goal";

const MonthStats: React.FC = () => {
    const {data: monthlySavingStats} = useMonthlySavingStats();

    if (!monthlySavingStats) return null;
    const monthStats = monthlySavingStats[monthlySavingStats.length - 2];

    return (<>
        <h2>{monthStats.yearMonth.toLabel()}</h2>
        <Goal
            performance={monthStats.balanceChange.smoothed / monthStats.balanceChangeTarget.smoothed}
            label={"Savings Goal"}
            sublabel={getEuroString(monthStats.balanceChange.smoothed) + " / " + getEuroString(monthStats.balanceChangeTarget.smoothed)}
        />
        <Goal
            performance={monthStats.deposits / monthStats.depositsTarget}
            label={"Deposits Goal"}
            sublabel={getEuroString(monthStats.deposits) + " / " + getEuroString(monthStats.depositsTarget)}
        />
        <div>-</div>
        <div>Leftovers:</div>
        <div>{getEuroString(monthStats.balanceChangeDeviation.smoothed)}</div>
    </>);
};

export default MonthStats;
