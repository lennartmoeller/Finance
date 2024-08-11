class Month {

    private readonly month: number;

    constructor(month: number) {
        if (month < 1 || month > 12) {
            throw new Error("Invalid month. Month must be between 1 and 12.");
        }
        this.month = month;
    }

    public getValue(): number {
        return this.month;
    }

    public next(): Month {
        return new Month((this.month % 12) + 1);
    }

    public toString(): string {
        return this.month.toString().padStart(2, '0');
    }

    public toLabel(): string {
        const months: Array<string> = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        return months[this.month - 1];
    }

    public static fromString(value: string): Month {
        return new Month(parseInt(value));
    }

    public static fromDate(date: Date): Month {
        return new Month(date.getMonth() + 1);
    }

}

export default Month;
