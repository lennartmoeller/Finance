import Quarter from "@/utils/Quarter";
import Year from "@/utils/Year";

class YearQuarter {
    private readonly year: Year;
    private readonly quarter: Quarter;

    constructor(year: Year, quarter: Quarter) {
        this.year = year;
        this.quarter = quarter;
    }

    public getYear(): Year {
        return this.year;
    }

    public getQuarter(): Quarter {
        return this.quarter;
    }

    public toString(): string {
        return `${this.year.toString()}-${this.quarter.toString()}`;
    }

    public static fromString(value: string): YearQuarter {
        const parts = value.split("-");
        return new YearQuarter(
            Year.fromString(parts[0]),
            Quarter.fromString(parts[1]),
        );
    }

    public static fromDate(date: Date): YearQuarter {
        return new YearQuarter(Year.fromDate(date), Quarter.fromDate(date));
    }
}

export default YearQuarter;
