import InputFormatter, { InputFormatterOptions } from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";
import Month from "@/utils/Month";
import Year from "@/utils/Year";
import YearMonth from "@/utils/YearMonth";

interface GermanYearMonthInputFormatterOptions extends InputFormatterOptions {
    defaultYear?: number;
}

class GermanYearMonthInputFormatter extends InputFormatter<YearMonth> {
    private readonly defaultYear: number | undefined;

    constructor(options: GermanYearMonthInputFormatterOptions = {}) {
        super(options);
        if (options.defaultYear !== undefined && options.defaultYear.toString().length !== 4) {
            throw new Error("Invalid default year");
        }
        this.defaultYear = options.defaultYear;
    }

    public valueToString(value: YearMonth | null): string {
        if (value === null) {
            return "";
        }

        const monthNumber = value.getMonth().getValue();
        const yearNumber = value.getYear().getValue();

        const month = monthNumber.toString().padStart(2, "0");
        const year = yearNumber.toString();

        return `${month}.${year}`;
    }

    public stringToValue(string: string): YearMonth | null {
        const match = new RegExp(/^([x\d]{2})\.([x\d]{4})$/).exec(string);
        if (!match) {
            return null;
        }

        const monthString: string = match[1].replace("x", "");
        const yearString: string = match[2].replace("x", "");

        const monthNumber: number = Number.parseInt(monthString, 10);
        const yearNumber: number = Number.parseInt(yearString, 10);

        if (Number.isNaN(monthNumber) || Number.isNaN(yearNumber) || monthNumber < 1 || monthNumber > 12) {
            return null;
        }

        return new YearMonth(new Year(yearNumber), new Month(monthNumber));
    }

    public onFocus(state: InputState<YearMonth>): InputState<YearMonth> {
        return {
            ...state,
            prediction: this.getPrediction(state.value),
        };
    }

    public onChange(before: InputState<YearMonth>, after: string): InputState<YearMonth> {
        if (/[^\d.]/.test(after)) {
            return before;
        }

        if (/\.\./.test(after)) {
            return before;
        }

        let value: string = "";

        if (before.value.length - after.length > 0) {
            value = after;
        } else {
            const parts: string[] = after.split(".");

            const month: string = parts[0];
            if (month !== "") {
                const monthNumber: number = Number.parseInt(month);
                if (month.length > 2 || Number.isNaN(monthNumber) || monthNumber > 12) {
                    return before;
                }
                const year: string | undefined = parts[1] ?? undefined;
                if (month.length === 2 || monthNumber > 1 || year !== undefined) {
                    value += month.padStart(2, "0") + ".";
                } else {
                    value += month;
                }
                if ((year ?? "") !== "") {
                    const yearNumber: number = Number.parseInt(year);
                    if (year.length > 4 || Number.isNaN(yearNumber)) {
                        return before;
                    }
                    value += year;
                }
            }
        }

        const prediction = this.getPrediction(value);

        return {
            ...super.onChange(before, value),
            prediction,
            value,
        };
    }

    private readonly getPrediction = (value: string): { label: string; value: YearMonth | null } => {
        const parts: Array<string> = value.split(".");

        parts[0] = (parts[0] || "xx").padEnd(2, "x");
        parts[1] = (parts[1] || this.defaultYear?.toString() || "xxxx").padEnd(4, "x");

        const predictionLabel: string = parts.join(".");
        return {
            label: predictionLabel,
            value: this.stringToValue(predictionLabel),
        };
    };
}

export default GermanYearMonthInputFormatter;
