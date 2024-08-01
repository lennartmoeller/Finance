class Half {

    private readonly half: number;

    constructor(half: number) {
        if (half < 1 || half > 2) {
            throw new Error("Invalid half. Half must be 1 or 2.");
        }
        this.half = half;
    }

    public getValue(): number {
        return this.half;
    }

    public toString(): string {
        return `H${this.half}`;
    }

    public static fromString(value: string): Half {
        if (!/^H[1-2]$/.test(value)) {
            throw new Error("Invalid half string. Must be 'H1' or 'H2'.");
        }
        return new Half(parseInt(value.charAt(1)));
    }

    public static fromDate(date: Date): Half {
        const month = date.getMonth() + 1;
        return new Half(Math.ceil(month / 6));
    }

}

export default Half;
