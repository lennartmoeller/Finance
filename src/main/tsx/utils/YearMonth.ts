import {Month} from './Month';
import {Year} from './Year';

export class YearMonth {

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

    public static fromDate(date: Date): YearMonth {
        return new YearMonth(Year.fromDate(date), Month.fromDate(date));
    }

}
