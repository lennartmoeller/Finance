import React from "react";

import {useGetQuery} from "@/api/useGetQuery";
import {Table} from "@/components/Table/Table";
import {TableBodyCell} from "@/components/Table/TableBodyCell";
import {TableBodyRow} from "@/components/Table/TableBodyRow";
import {TableHeaderCell} from "@/components/Table/TableHeaderCell";
import {Stats, StatsDTO, statsMapper} from "@/types/Stats";
import {getMonths} from "@/utils/date";
import {YearMonth} from "@/utils/YearMonth";

const Stats: React.FC = () => {
    const {data: stats, error, isLoading} = useGetQuery<StatsDTO, Stats>('stats', statsMapper.fromDTO);

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!stats) return <div>No data available</div>;

    const tableData = stats.categoryStats;

    const months: Array<YearMonth> = getMonths(stats.startDate, stats.endDate);

    return <Table
        data={tableData}
        header={<>
            <TableHeaderCell>Category</TableHeaderCell>
            {months.map((month) => {
                const monthString: string = month.toString();
                const monthLabel: string = month.toLabel();
                return <TableHeaderCell key={monthString} width="200px">{monthLabel}</TableHeaderCell>;
            })}
        </>}
        body={(element) =>
            // eslint-disable-next-line react/jsx-no-undef
            <TableBodyRow id={element.category.id.toString()}>
                <TableBodyCell>{element.category.label}</TableBodyCell>
                {months.map((month) => {
                    const monthString: string = month.toString();
                    const surplus: number | undefined = element.statistics[monthString]?.surplus;
                    return <TableBodyCell key={monthString}>{surplus}</TableBodyCell>;
                })}
            </TableBodyRow>
        }
    />;

};

export default Stats;
