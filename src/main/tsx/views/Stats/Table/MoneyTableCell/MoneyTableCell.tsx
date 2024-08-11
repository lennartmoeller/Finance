import React from "react";

import TableBodyCell from "@/components/Table/TableBodyCell";
import CellStats from "@/types/CellStats";
import {getEuroString} from "@/utils/money";
import {StatsMode} from "@/views/Stats/Stats";
import PerformanceArrow from "@/views/Stats/Table/MoneyTableCell/PerformanceArrow";
import StyledMoneyString from "@/views/Stats/Table/MoneyTableCell/styles/StyledMoneyString";
import StyledMoneyTableCell from "@/views/Stats/Table/MoneyTableCell/styles/StyledMoneyTableCell";

interface MoneyTableCellProps {
    columnCount?: number,
    headerLevel?: 1 | 2,
    mode: StatsMode,
    stats: CellStats,
}

const MoneyTableCell: React.FC<MoneyTableCellProps> = ({columnCount = 1, headerLevel, mode, stats,}) => {
    const centsValue: number = stats.surplus[mode.processing] * columnCount;
    const euroString: string = getEuroString(centsValue);
    const performance: number | undefined = stats.performance?.[mode.processing];

    return (
        <TableBodyCell
            colspan={columnCount}
            headerLevel={headerLevel}
            horAlign="center">
            <StyledMoneyTableCell>
                <StyledMoneyString $zero={centsValue === 0}>{euroString}</StyledMoneyString>
                <PerformanceArrow performance={performance}/>
            </StyledMoneyTableCell>
        </TableBodyCell>
    );
};

export default MoneyTableCell;
