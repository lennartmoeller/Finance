import React, {ReactElement} from "react";

import Table from "@/components/Table/Table";
import TableBodyHierarchyCell from "@/components/Table/TableBodyHierarchyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableHierarchyLevel from "@/components/Table/TableHierarchyLevel";
import TableRow from "@/components/Table/TableRow";
import CategorySmoothType from "@/types/CategorySmoothType";
import CategoryStatsNode from "@/types/CategoryStatsNode";
import Stats from "@/types/Stats";
import {getMonths} from "@/utils/date";
import YearMonth from "@/utils/YearMonth";
import {StatsMode} from "@/views/Stats/Stats";
import MoneyTableCell from "@/views/Stats/Table/MoneyTableCell/MoneyTableCell";

interface StatsTableProps {
    stats: Stats;
    mode: StatsMode;
}

const StatsTable: React.FC<StatsTableProps> = ({stats, mode}) => {
    const months: Array<YearMonth> = getMonths(stats.startDate, stats.endDate);

    const tableHeader: ReactElement =
        <TableRow>
            <TableHeaderCell
                sticky="topAndLeft"
                width={220}
                zIndex={2}>
                Category
            </TableHeaderCell>
            <TableHeaderCell
                horAlign="center"
                sticky="top"
                width={120}
                zIndex={1}>
                Average
            </TableHeaderCell>
            {months.map((month: YearMonth) => {
                const monthString: string = month.toString();
                const monthLabel: string = month.toLabel();
                const width: number = month.lengthOfMonth() * 4;
                return (
                    <TableHeaderCell
                        key={monthString}
                        horAlign="center"
                        sticky="top"
                        width={width}
                        zIndex={1}>
                        {monthLabel}
                    </TableHeaderCell>
                );
            })}
        </TableRow>;

    const getTableBodyRowGroup = (categoryStats: CategoryStatsNode): ReactElement =>
        <TableHierarchyLevel key={categoryStats.category.id}>
            <TableRow>
                <TableBodyHierarchyCell
                    sticky="left"
                    zIndex={1}>
                    {categoryStats.category.label}
                </TableBodyHierarchyCell>
                <MoneyTableCell mode={mode} stats={categoryStats.stats.mean}/>
                {months.map((month: YearMonth) => {
                    const getBodyCellColumnCount = (element: CategoryStatsNode, month: YearMonth): number => {
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
                    };
                    const columnCount: number = mode.shared ? getBodyCellColumnCount(categoryStats, month) : 1;
                    if (columnCount < 1) return <></>;

                    const monthString: string = month.toString();
                    return (
                        <MoneyTableCell
                            key={monthString}
                            columnCount={columnCount}
                            mode={mode}
                            stats={categoryStats.stats.monthly[monthString]}
                        />
                    );
                })}
            </TableRow>
            {categoryStats.children.map(getTableBodyRowGroup)}
        </TableHierarchyLevel>;

    return (
        <Table
            data={stats.categoryStats}
            header={tableHeader}
            body={element => getTableBodyRowGroup(element)}
        />
    );
};

export default StatsTable;
