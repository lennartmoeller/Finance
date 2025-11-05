class Quarter {
    private readonly quarter: number;

    constructor(quarter: number) {
        if (quarter < 1 || quarter > 4) {
            throw new Error("Invalid quarter. Quarter must be between 1 and 4.");
        }
        this.quarter = quarter;
    }

    public getValue(): number {
        return this.quarter;
    }

    public toString(): string {
        return `Q${this.quarter}`;
    }

    public static fromString(value: string): Quarter {
        if (!/^Q[1-4]$/.test(value)) {
            throw new Error("Invalid quarter string. Must be 'Q1', 'Q2', 'Q3', or 'Q4'.");
        }
        return new Quarter(parseInt(value.charAt(1)));
    }

    public static fromDate(date: Date): Quarter {
        const month = date.getMonth() + 1;
        return new Quarter(Math.ceil(month / 3));
    }
}

export default Quarter;
