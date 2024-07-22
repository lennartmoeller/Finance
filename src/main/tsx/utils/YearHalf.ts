import {Half} from "@/utils/Half";
import {Year} from "@/utils/Year";

export class YearHalf {

    private readonly year: Year;
    private readonly half: Half;

    constructor(year: Year, half: Half) {
        this.year = year;
        this.half = half;
    }

    public getYear(): Year {
        return this.year;
    }

    public getHalf(): Half {
        return this.half;
    }

    public toString(): string {
        return `${this.year.toString()}-${this.half.toString()}`;
    }

    public static fromString(value: string): YearHalf {
        const parts = value.split('-');
        return new YearHalf(Year.fromString(parts[0]), Half.fromString(parts[1]));
    }

    public static fromDate(date: Date): YearHalf {
        return new YearHalf(Year.fromDate(date), Half.fromDate(date));
    }

}
