import Month from "@/utils/Month";
import Year from "@/utils/Year";

class YearMonth {

    private readonly year: Year;
    private readonly month: Month;

    constructor(year: Year, month: Month) {
        this.year = year;
        this.month = month;
    }

    public getYear(): Year {
        return this.year;
    }

    public getMonth(): Month {
        return this.month;
    }

    public lengthOfMonth(): number {
        return (new Date(this.year.getValue(), this.month.getValue(), 0)).getDate();
    }

    public monthsTo(other: YearMonth): number {
        const thisTotalMonths = this.year.getValue() * 12 + this.month.getValue();
        const otherTotalMonths = other.getYear().getValue() * 12 + other.getMonth().getValue();
        return otherTotalMonths - thisTotalMonths;
    }

    public previous(): YearMonth {
        if (this.month.getValue() === 1) {
            return new YearMonth(this.year.previous(), new Month(12));
        } else {
            return new YearMonth(this.year, this.month.previous());
        }
    }

    public next(): YearMonth {
        if (this.month.getValue() === 12) {
            return new YearMonth(this.year.next(), new Month(1));
        } else {
            return new YearMonth(this.year, this.month.next());
        }
    }

    public toString(): string {
        return `${this.year.toString()}-${this.month.toString()}`;
    }

    public toLabel(): string {
        return `${this.month.toLabel()} ${this.year.toLabel()}`;
    }

    public static fromString(value: string): YearMonth {
        const parts = value.split('-');
        return new YearMonth(Year.fromString(parts[0]), Month.fromString(parts[1]));
    }

    public static toString(yearMonth: YearMonth): string {
        return yearMonth.toString();
    }

    public static fromDate(date: Date): YearMonth {
        return new YearMonth(Year.fromDate(date), Month.fromDate(date));
    }

}

export default YearMonth;
