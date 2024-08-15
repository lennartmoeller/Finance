import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";

/**
 * Input formatter for cent values.
 */
class DateInputFormatter extends InputFormatter<Date> {

    private readonly defaultYear: number | undefined;
    private readonly defaultMonth: number | undefined;

    constructor(defaultYear?: number, defaultMonth?: number) {
        super();
        this.defaultYear = defaultYear;
        this.defaultMonth = defaultMonth;
    }

    /**
     * @inheritDoc
     */
    toInputState = (date: Date | null): InputState => {
        if (date === null) {
            return {value: ''};
        }

        const dayNumber = date.getDate();
        const monthNumber = this.defaultMonth ?? (date.getMonth() + 1);
        const yearNumber = this.defaultYear ?? (date.getFullYear());

        const day = dayNumber.toString().padStart(2, '0');
        const month = monthNumber.toString().padStart(2, '0');
        const year = yearNumber.toString();

        return {value: `${day}.${month}.${year}`};
    };

    /**
     * @inheritDoc
     */
    onChange = (before: InputState, after: string): InputState => {
        // Ensure valid characters: digits, dot
        if (/[^\d.]/.test(after)) {
            return before;
        }

        // Ensure no doubled dots
        if (/\.\./.test(after)) {
            return before;
        }

        // Deletion of characters should always be possible
        if (before.value.length - after.length > 0) {
            return {value: after};
        }

        const parts = after.split('.');

        let day: string = parts[0];
        let month: string | undefined = parts[1] ?? undefined;
        let year: string | undefined = parts[2] ?? undefined;

        if (day === '') {
            return {value: ''};
        }
        const dayNumber = parseInt(day);
        if (isNaN(dayNumber) || dayNumber > 31) {
            return before;
        }
        if (dayNumber > 3) {
            day = day.padStart(2, '0');
        }
        if (day.length === 2) {
            month = month ?? '';
        }
        if (month === undefined) {
            return {value: `${day}`};
        }
        day = day.padStart(2, '0');
        if (month === '') {
            return {value: `${day}.`};
        }
        const monthNumber = parseInt(month);
        if (isNaN(monthNumber) || monthNumber > 12) {
            return before;
        }
        if (monthNumber > 1) {
            month = month.padStart(2, '0');
        }
        if (month.length === 2) {
            year = year ?? '';
        }
        if (year === undefined) {
            return {value: `${day}.${month}`};
        }
        month = month.padStart(2, '0');
        if (year === '') {
            return {value: `${day}.${month}.`};
        }
        const yearNumber = parseInt(year);
        if (isNaN(yearNumber) || year.length > 4) {
            return before;
        }
        return {value: `${day}.${month}.${year}`};
    };

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState): Date | null => {
        return null;
    };
}

export default DateInputFormatter;
