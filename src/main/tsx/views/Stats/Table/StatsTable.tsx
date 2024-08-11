import React, {ReactElement} from "react";

import Table from "@/components/Table/Table";
import TableBodyHierarchyCell from "@/components/Table/TableBodyHierarchyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableHierarchyLevel from "@/components/Table/TableHierarchyLevel";
import TableRow from "@/components/Table/TableRow";
import CategorySmoothType from "@/types/CategorySmoothType";
import CategoryStatsNode from "@/types/CategoryStatsNode";
import RowStats from "@/types/RowStats";
import Stats from "@/types/Stats";
import {getMonths} from "@/utils/date";
import YearMonth from "@/utils/YearMonth";
import {StatsMode} from "@/views/Stats/Stats";
import MoneyTableCell from "@/views/Stats/Table/MoneyTableCell/MoneyTableCell";

interface StatsTableRow {
    id: string;
    label: string;
    stats: RowStats;
    headerLevel?: 1 | 2;
    smoothType?: CategorySmoothType;
    open?: boolean;
    children: Array<StatsTableRow>;
}

interface StatsTableProps {
    mode: StatsMode;
    stats: Stats;
}

const StatsTable: React.FC<StatsTableProps> = ({mode, stats,}) => {
    const months: Array<YearMonth> = getMonths(stats.startDate, stats.endDate);

    const categoryStatsNodesToStatsTableRows = (categoryStatsNodes: Array<CategoryStatsNode>): Array<StatsTableRow> =>
        categoryStatsNodes.map((categoryStatsNode: CategoryStatsNode): StatsTableRow =>
            ({
                id: String(categoryStatsNode.category.id),
                label: categoryStatsNode.category.label,
                stats: categoryStatsNode.stats,
                smoothType: categoryStatsNode.category.smoothType,
                open: false,
                children: categoryStatsNodesToStatsTableRows(categoryStatsNode.children),
            })
        );

    const tableData: Array<StatsTableRow> = [
        {
            id: "incomes",
            label: "Incomes",
            headerLevel: 2,
            stats: stats.incomeStats.totalStats,
            children: categoryStatsNodesToStatsTableRows(stats.incomeStats.categoryStats)
        },
        {
            id: "expenses",
            label: "Expenses",
            headerLevel: 2,
            stats: stats.expenseStats.totalStats,
            children: categoryStatsNodesToStatsTableRows(stats.expenseStats.categoryStats)
        },
        {
            id: "surplus",
            label: "Surplus",
            headerLevel: 2,
            stats: stats.totalStats,
            children: []
        }
    ];

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

    const getTableBodyRowGroup = (rowData: StatsTableRow): ReactElement =>
        <TableHierarchyLevel
            key={rowData.id}
            initiallyOpen={rowData.open}
        >
            <TableRow>
                <TableBodyHierarchyCell
                    headerLevel={rowData.headerLevel}
                    sticky="left"
                    zIndex={1}>
                    {rowData.label}
                </TableBodyHierarchyCell>
                <MoneyTableCell
                    headerLevel={rowData.headerLevel}
                    mode={mode}
                    stats={rowData.stats.mean}
                />
                {months.map((month: YearMonth) => {
                    const getBodyCellColumnCount = (smoothType: CategorySmoothType, month: YearMonth): number => {
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
                    const columnCount: number = mode.shared && rowData.smoothType ? getBodyCellColumnCount(rowData.smoothType, month) : 1;
                    if (columnCount < 1) return <></>;

                    const monthString: string = month.toString();
                    return (
                        <MoneyTableCell
                            key={monthString}
                            columnCount={columnCount}
                            headerLevel={rowData.headerLevel}
                            mode={mode}
                            stats={rowData.stats.monthly[monthString]}
                        />
                    );
                })}
            </TableRow>
            {rowData.children.map(getTableBodyRowGroup)}
        </TableHierarchyLevel>;

    return (
        <Table
            data={tableData}
            header={tableHeader}
            body={getTableBodyRowGroup}
        />
    );
};

export default StatsTable;
