import {YearMonth} from "@/utils/YearMonth";

export function getMonths(from: Date, to: Date): Array<YearMonth> {
    const months: YearMonth[] = new Array<YearMonth>();
    const current: Date = new Date(from);
    while (current <= to) {
        months.push(YearMonth.fromDate(current));
        current.setMonth(current.getMonth() + 1);
    }
    return months;
}
