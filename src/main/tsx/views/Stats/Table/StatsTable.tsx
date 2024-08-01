import React, {ReactElement} from "react";

import Table from "@/components/Table/Table";
import TableBodyHierarchyCell from "@/components/Table/TableBodyHierarchyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableHierarchyLevel from "@/components/Table/TableHierarchyLevel";
import TableRow from "@/components/Table/TableRow";
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
                <TableBodyHierarchyCell sticky="left" zIndex={1}>{categoryStats.category.label}</TableBodyHierarchyCell>
                {months.map((month: YearMonth) => {
                    const monthString: string = month.toString();
                    return (
                        <MoneyTableCell
                            key={monthString}
                            categoryStats={categoryStats}
                            endDate={stats.endDate}
                            mode={mode}
                            month={month}
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
