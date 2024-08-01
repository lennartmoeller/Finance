import React from "react";
import TableBodyCell from "@/components/Table/TableBodyCell";
import CategorySmoothType from "@/types/CategorySmoothType";
import CategoryStatsNode from "@/types/CategoryStatsNode";
import {getEuroString} from "@/utils/money";
import YearMonth from "@/utils/YearMonth";
import {StatsMode} from "@/views/Stats/Stats";
import StyledMoneyTableCell from "@/views/Stats/Table/MoneyTableCell/styles/StyledMoneyTableCell";
import StyledMoneyString from "@/views/Stats/Table/MoneyTableCell/styles/StyledMoneyString";
import PerformanceArrow from "@/views/Stats/Table/MoneyTableCell/PerformanceArrow";

interface MoneyTableCellProps {
    categoryStats: CategoryStatsNode;
    endDate: Date;
    mode: StatsMode;
    month: YearMonth;
}

const MoneyTableCell: React.FC<MoneyTableCellProps> = ({categoryStats, endDate, mode, month,}) => {
    const getBodyCellColumnCount = (element: CategoryStatsNode, month: YearMonth): number => {
        const smoothType: CategorySmoothType = element.category.smoothType;
        const endMonth: YearMonth = YearMonth.fromDate(endDate);
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

    const monthString: string = month.toString();

    let centsValue: number = 0;
    let columnCount: number = 1;

    if (mode.shared) {
        columnCount = getBodyCellColumnCount(categoryStats, month);
        if (columnCount < 1) return <></>;

        let monthToCheck: YearMonth = month;
        for (let i: number = 0; i < columnCount; i++) {
            const monthString: string = monthToCheck.toString();
            centsValue += categoryStats.statistics[monthString]?.surplus[mode.processing] ?? 0;
            monthToCheck = monthToCheck.next();
        }
    } else {
        centsValue = categoryStats.statistics[monthString]?.surplus[mode.processing] ?? 0;
    }

    const euroString: string = getEuroString(centsValue ?? 0);
    const performance: number | undefined = categoryStats.statistics[monthString]?.performance?.[mode.processing];

    return (
        <TableBodyCell
            horAlign="center"
            colspan={columnCount}>
            <StyledMoneyTableCell>
                <StyledMoneyString $zero={centsValue === 0}>{euroString}</StyledMoneyString>
                <PerformanceArrow performance={performance}/>
            </StyledMoneyTableCell>
        </TableBodyCell>
    );
};

export default MoneyTableCell;
