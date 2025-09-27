import React, { ReactElement, useState } from "react";

import Icon from "@/components/Icon/Icon";
import HierarchyContext from "@/components/Table/context/HierarchyContext";
import Table from "@/components/Table/Table";
import TableBodyHierarchyCell from "@/components/Table/TableBodyHierarchyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import CategorySmoothType from "@/types/CategorySmoothType";
import CategoryStats from "@/types/CategoryStats";
import MonthlyCategoryStats from "@/types/MonthlyCategoryStats";
import RowStats from "@/types/RowStats";
import { getMonths } from "@/utils/date";
import YearMonth from "@/utils/YearMonth";
import MoneyTableCell from "@/views/StatsView/Table/MoneyTableCell/MoneyTableCell";

interface StatsTableRowContentProps {
    rowData: StatsTableRow & { level: number };
    months: Array<YearMonth>;
    stats: MonthlyCategoryStats;
    smoothed: boolean;
    merged: boolean;
}

const StatsTableRowContent: React.FC<StatsTableRowContentProps> = ({
    rowData,
    months,
    stats,
    smoothed,
    merged,
}) => {
    const childrenVisibleState = useState<boolean>(true);
    const hasChildrenState = useState<boolean>(rowData.children.length > 0);

    return (
        <HierarchyContext.Provider
            value={{
                level: rowData.level + 1,
                childrenVisible: childrenVisibleState,
                hasChildren: hasChildrenState,
            }}
        >
            <>
                <TableBodyHierarchyCell
                    headerLevel={rowData.headerLevel}
                    sticky="left"
                >
                    {rowData.icon && (
                        <Icon id={rowData.icon} size={16} opacity={0.6} />
                    )}
                    {rowData.label}
                </TableBodyHierarchyCell>
                <MoneyTableCell
                    headerLevel={rowData.headerLevel}
                    stats={rowData.stats.mean}
                    smoothed={smoothed}
                />
                {months.map((month: YearMonth) => {
                    const getBodyCellColumnCount = (
                        smoothType: CategorySmoothType,
                        month: YearMonth,
                    ): number => {
                        const endMonth: YearMonth = YearMonth.fromDate(
                            stats.endDate!,
                        );
                        const max: number = month.monthsTo(endMonth) + 1;

                        const output: number = (() => {
                            switch (smoothType) {
                                case CategorySmoothType.DAILY:
                                case CategorySmoothType.MONTHLY:
                                    return 1;
                                case CategorySmoothType.QUARTER_YEARLY:
                                    return month.getMonth().getValue() % 3 === 1
                                        ? 3
                                        : 0;
                                case CategorySmoothType.HALF_YEARLY:
                                    return month.getMonth().getValue() % 6 === 1
                                        ? 6
                                        : 0;
                                case CategorySmoothType.YEARLY:
                                    return month.getMonth().getValue() === 1
                                        ? 12
                                        : 0;
                            }
                        })();

                        return Math.max(0, Math.min(max, output));
                    };
                    const columnCount: number =
                        merged && rowData.smoothType
                            ? getBodyCellColumnCount(rowData.smoothType, month)
                            : 1;
                    if (columnCount < 1) return <></>;

                    const monthString: string = month.toString();
                    return (
                        <MoneyTableCell
                            key={monthString}
                            columnCount={columnCount}
                            headerLevel={rowData.headerLevel}
                            stats={rowData.stats.monthly[monthString]}
                            smoothed={smoothed}
                        />
                    );
                })}
            </>
        </HierarchyContext.Provider>
    );
};

interface StatsTableRow {
    id: string;
    label: string;
    icon?: string;
    stats: RowStats;
    headerLevel?: 1 | 2;
    smoothType?: CategorySmoothType;
    open?: boolean;
    children: Array<StatsTableRow>;
}

interface StatsTableProps {
    stats: MonthlyCategoryStats;
    smoothed: boolean;
    merged: boolean;
}

const StatsTable: React.FC<StatsTableProps> = ({ smoothed, merged, stats }) => {
    const months: Array<YearMonth> =
        stats.startDate === null || stats.endDate === null
            ? []
            : getMonths(stats.startDate, stats.endDate);

    const categoryStatsNodesToStatsTableRows = (
        categoryStatsNodes: Array<CategoryStats>,
    ): Array<StatsTableRow> =>
        categoryStatsNodes.map(
            (categoryStatsNode: CategoryStats): StatsTableRow => ({
                id: String(categoryStatsNode.category.id),
                label: categoryStatsNode.category.label,
                icon: categoryStatsNode.category.icon ?? undefined,
                stats: categoryStatsNode.stats,
                smoothType: categoryStatsNode.category.smoothType,
                open: true,
                children: categoryStatsNodesToStatsTableRows(
                    categoryStatsNode.children,
                ),
            }),
        );

    const tableData: Array<StatsTableRow> = [
        {
            id: "incomes",
            label: "Incomes",
            headerLevel: 2,
            stats: stats.stats["INCOME"].totalStats,
            children: categoryStatsNodesToStatsTableRows(
                stats.stats["INCOME"].categoryStats,
            ),
        },
        {
            id: "investments",
            label: "Investments",
            headerLevel: 2,
            stats: stats.stats["INVESTMENT"].totalStats,
            children: categoryStatsNodesToStatsTableRows(
                stats.stats["INVESTMENT"].categoryStats,
            ),
        },
        {
            id: "expenses",
            label: "Expenses",
            headerLevel: 2,
            stats: stats.stats["EXPENSE"].totalStats,
            children: categoryStatsNodesToStatsTableRows(
                stats.stats["EXPENSE"].categoryStats,
            ),
        },
        {
            id: "surplus",
            label: "Surplus",
            headerLevel: 2,
            stats: stats.totalStats,
            children: [],
        },
    ];

    const tableHeader: ReactElement = (
        <TableRow>
            <TableHeaderCell sticky="topAndLeft" width={220} zIndex={3}>
                Category
            </TableHeaderCell>
            <TableHeaderCell horAlign="center" sticky="top" width={120}>
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
                    >
                        {monthLabel}
                    </TableHeaderCell>
                );
            })}
        </TableRow>
    );

    // Flatten hierarchical data for the new Table API
    const flattenTableData = (
        rows: Array<StatsTableRow>,
        level: number = 0,
    ): Array<StatsTableRow & { level: number }> => {
        const result: Array<StatsTableRow & { level: number }> = [];
        for (const row of rows) {
            result.push({ ...row, level });
            if (row.children.length > 0) {
                result.push(...flattenTableData(row.children, level + 1));
            }
        }
        return result;
    };

    const flatData = flattenTableData(tableData);

    return (
        <Table
            header={tableHeader}
            body={{
                data: flatData,
                key: (rowData: StatsTableRow & { level: number }) => rowData.id,
                content: (rowData: StatsTableRow & { level: number }) => (
                    <StatsTableRowContent
                        rowData={rowData}
                        months={months}
                        stats={stats}
                        smoothed={smoothed}
                        merged={merged}
                    />
                ),
                properties: () => ({}),
            }}
        />
    );
};

export default StatsTable;
