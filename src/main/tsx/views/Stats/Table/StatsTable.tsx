import React, {ReactElement} from "react";

import {Table} from "@/components/Table/Table";
import {TableBodyCell} from "@/components/Table/TableBodyCell";
import {TableBodyRow} from "@/components/Table/TableBodyRow";
import {TableBodyRowGroup} from "@/components/Table/TableBodyRowGroup";
import {TableHeaderCell} from "@/components/Table/TableHeaderCell";
import {TableHeaderRow} from "@/components/Table/TableHeaderRow";
import {CategorySmoothType} from "@/types/CategorySmoothType";
import {CategoryStatsNode} from "@/types/CategoryStatsNode";
import {Stats} from "@/types/Stats";
import {getMonths} from "@/utils/date";
import {getEuroString} from "@/utils/money";
import {YearMonth} from "@/utils/YearMonth";

interface StatsTableProps {
    stats: Stats;
    mode: 'surplus' | 'smoothedSurplus';
}

const StatsTable: React.FC<StatsTableProps> = ({stats, mode}) => {
    const months: Array<YearMonth> = getMonths(stats.startDate, stats.endDate);

    function getBodyCellColumnCount(element: CategoryStatsNode, month: YearMonth): number {
        const smoothType: CategorySmoothType = element.category.smoothType;
        const endMonth: YearMonth = YearMonth.fromDate(stats.endDate);
        const max: number = month.monthsTo(endMonth) + 1;

        const output: number = (() => {
            switch (smoothType) {
                case CategorySmoothType.DAILY:
                case CategorySmoothType.MONTHLY:
                    return 1;
                case CategorySmoothType.QUARTER_YEARLY:
                    return month.getMonth().getValue() % 3 === 1 ? 3 : 0;
                case CategorySmoothType.HALF_YEARLY:
                    return month.getMonth().getValue() % 6 === 1 ? 6 : 0;
                case CategorySmoothType.YEARLY:
                    return month.getMonth().getValue() === 1 ? 12 : 0;
            }
        })();

        return Math.max(0, Math.min(max, output));
    }

    const tableHeader: ReactElement =
        <TableHeaderRow>
            <TableHeaderCell
                sticky="topAndLeft"
                width="220px"
                zIndex={2}>
                Category
            </TableHeaderCell>
            {months.map((month: YearMonth) => {
                const monthString: string = month.toString();
                const monthLabel: string = month.toLabel();
                const width: string = (month.lengthOfMonth() * 4) + "px";
                return <TableHeaderCell
                    align="center"
                    key={monthString}
                    sticky="top"
                    width={width}
                    zIndex={1}>
                    {monthLabel}
                </TableHeaderCell>;
            })}
        </TableHeaderRow>;

    const getTableBodyRowGroup = (element: CategoryStatsNode): ReactElement =>
        <TableBodyRowGroup>
            <TableBodyRow>
                <TableBodyCell sticky="left" zIndex={1}>{element.category.label}</TableBodyCell>
                {months.map((month: YearMonth) => {
                    let centsValue: number = 0;
                    let columnCount: number = 1;

                    if (mode === 'smoothedSurplus') {
                        columnCount = getBodyCellColumnCount(element, month);
                        if (columnCount < 1) return <></>;

                        let monthToCheck: YearMonth = month;
                        for (let i: number = 0; i < columnCount; i++) {
                            const monthString: string = monthToCheck.toString();
                            centsValue += element.statistics[monthString]?.[mode] ?? 0;
                            monthToCheck = monthToCheck.next();
                        }
                    } else {
                        const monthString: string = month.toString();
                        centsValue = element.statistics[monthString]?.[mode] ?? 0;
                    }

                    const euroString: string = centsValue ? getEuroString(centsValue) : "";

                    return <TableBodyCell
                        key={month.toString()}
                        align="center"
                        colspan={columnCount}>
                        <span style={{fontFamily: "monospace"}}>{euroString}</span>
                    </TableBodyCell>;
                })}
            </TableBodyRow>
            {element.children.map(getTableBodyRowGroup)}
        </TableBodyRowGroup>;

    return (
        <Table
            data={stats.categoryStats}
            header={tableHeader}
            body={getTableBodyRowGroup}
        />
    );
};

export default StatsTable;
