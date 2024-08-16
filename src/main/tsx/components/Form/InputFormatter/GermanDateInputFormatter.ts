import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";
import FieldErrorType from "@/components/Form/types/FieldErrorType";

/**
 * Input formatter for cent values.
 */
class GermanDateInputFormatter extends InputFormatter<Date> {

    private readonly defaultYear: number | undefined;
    private readonly defaultMonth: number | undefined;

    constructor(defaultYear?: number, defaultMonth?: number) {
        super();
        if (defaultYear !== undefined && defaultYear.toString().length !== 4) {
            throw new Error('Invalid default year');
        }
        this.defaultYear = defaultYear;
        if (defaultMonth !== undefined && (defaultMonth < 1 || defaultMonth > 12)) {
            throw new Error('Invalid default month');
        }
        this.defaultMonth = defaultMonth;
    }

    /**
     * @inheritDoc
     */
    toInputState = (date: Date | null): InputState<Date> => ({value: this.dateToGermanDateString(date)});

    /**
     * @inheritDoc
     */
    onFocus = (state: InputState<Date>): InputState<Date> => ({
        value: state.value,
        prediction: this.getPrediction(state.value),
    });

    /**
     * @inheritDoc
     */
    onChange = (before: InputState<Date>, after: string): InputState<Date> => {
        // Ensure valid characters: digits, dot
        if (/[^\d.]/.test(after)) {
            return before;
        }

        // Ensure no doubled dots
        if (/\.\./.test(after)) {
            return before;
        }

        let value: string = '';

        // Deletion of characters should always be possible
        if (before.value.length - after.length > 0) {
            value = after;
        } else {
            const parts: string[] = after.split('.');

            const day: string = parts[0];
            if (day !== '') {
                const dayNumber: number = parseInt(day);
                if (day.length > 2 || isNaN(dayNumber) || dayNumber > 31) {
                    return before;
                }
                const month: string | undefined = parts[1] ?? undefined;
                if (day.length === 2 || dayNumber > 3 || month !== undefined) {
                    value += day.padStart(2, '0') + '.';
                } else {
                    value += day;
                }
                if ((month ?? '') !== '') {
                    const monthNumber: number = parseInt(month);
                    if (month.length > 2 || isNaN(monthNumber) || monthNumber > 12) {
                        return before;
                    }
                    const year: string | undefined = parts[2] ?? undefined;
                    if (month.length === 2 || monthNumber > 1 || year !== undefined) {
                        value += month.padStart(2, '0') + '.';
                    } else {
                        value += month;
                    }
                    if ((year ?? '') !== '') {
                        const yearNumber: number = parseInt(year);
                        if (year.length > 4 || isNaN(yearNumber)) {
                            return before;
                        }
                        value += year;
                    }
                }
            }
        }

        const prediction = this.getPrediction(value);

        return {value, prediction};
    };

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState<Date>): Date | null => {
        return state.prediction?.value ?? null;
    };

    private getPrediction = (value: string): { label: string, value: Date | null } => {
        const parts: Array<string> = value.split('.');

        parts[0] = (parts[0] || 'xx').padEnd(2, 'x');
        parts[1] = (parts[1] || this.defaultMonth?.toString().padStart(2, '0') || 'xx').padEnd(2, 'x');
        parts[2] = (parts[2] || this.defaultYear?.toString() || 'xxxx').padEnd(4, 'x');

        const predictionLabel: string = parts.join('.');
        return {
            label: predictionLabel,
            value: this.germanDateStringToDate(predictionLabel)
        };
    };

    private dateToGermanDateString = (date: Date | null): string => {
        if (date === null) {
            return '';
        }

        const dayNumber = date.getDate();
        const monthNumber = date.getMonth() + 1;
        const yearNumber = date.getFullYear();

        const day = dayNumber.toString().padStart(2, '0');
        const month = monthNumber.toString().padStart(2, '0');
        const year = yearNumber.toString();

        return `${day}.${month}.${year}`;
    };

    private germanDateStringToDate = (dateString: string): Date | null => {
        const match = dateString.match(/^(\d{2})\.(\d{2})\.(\d{4})$/);
        if (!match) {
            return null;
        }

        const day: number = parseInt(match[1], 10);
        const month: number = parseInt(match[2], 10) - 1;
        const year: number = parseInt(match[3], 10);

        const date: Date = new Date(year, month, day);

        if (date.getFullYear() !== year || date.getMonth() !== month || date.getDate() !== day) {
            return null;
        }
        return date;
    };

}

export default GermanDateInputFormatter;
