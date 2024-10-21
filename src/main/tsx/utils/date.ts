import YearMonth from "@/utils/YearMonth";

export const getMonths = (from: Date, to: Date): Array<YearMonth> => {
    const months: Array<YearMonth> = new Array<YearMonth>();
    const current: Date = new Date(from);
    while (current <= to) {
        months.push(YearMonth.fromDate(current));
        current.setMonth(current.getMonth() + 1);
    }
    return months;
};

export const dateToString = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed, so we add 1
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
};
