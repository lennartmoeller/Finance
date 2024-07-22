export class Year {

    private readonly year: number;

    constructor(year: number) {
        this.year = year;
    }

    public getValue(): number {
        return this.year;
    }

    public toString(): string {
        return this.year.toString();
    }

    public toLabel(): string {
        return this.year.toString();
    }

    public static fromString(value: string): Year {
        return new Year(parseInt(value));
    }

    public static fromDate(date: Date): Year {
        return new Year(date.getFullYear());
    }

}