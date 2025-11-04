import React from "react";

import PerformanceArrow from "@/components/PerformanceArrow/PerformanceArrow";
import TableBodyCell from "@/components/TableOld/TableBodyCell";
import CellStats from "@/types/CellStats";
import { getEuroString } from "@/utils/money";
import StyledMoneyString from "@/views/StatsView/Table/MoneyTableCell/styles/StyledMoneyString";
import StyledMoneyTableCell from "@/views/StatsView/Table/MoneyTableCell/styles/StyledMoneyTableCell";

interface MoneyTableCellProps {
    columnCount?: number;
    headerLevel?: 1 | 2;
    stats: CellStats;
    smoothed: boolean;
}

const MoneyTableCell: React.FC<MoneyTableCellProps> = ({
    columnCount = 1,
    headerLevel,
    stats,
    smoothed,
}) => {
    const statsMetricKey: "raw" | "smoothed" = smoothed ? "smoothed" : "raw";
    const centsValue: number = stats.surplus[statsMetricKey] * columnCount;
    const euroString: string = getEuroString(centsValue, 12);
    const performance: number | undefined = stats.performance?.[statsMetricKey];

    return (
        <TableBodyCell
            colspan={columnCount}
            headerLevel={headerLevel}
            horAlign="center"
        >
            <StyledMoneyTableCell>
                <StyledMoneyString $zero={centsValue === 0}>
                    {euroString}
                </StyledMoneyString>
                <PerformanceArrow performance={performance} />
            </StyledMoneyTableCell>
        </TableBodyCell>
    );
};

export default MoneyTableCell;
