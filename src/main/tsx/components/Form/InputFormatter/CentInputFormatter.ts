import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";

/**
 * Input formatter for cent values.
 */
class CentInputFormatter extends InputFormatter<number> {

    /**
     * @inheritDoc
     */
    toInputState = (cents: number | null): InputState<number> => {
        if (cents === null) {
            return {value: ''};
        }

        const isNegative = cents < 0;
        const absoluteCents = Math.abs(cents);

        let formattedValue = (absoluteCents / 100).toFixed(2).replace('.', ',');
        formattedValue = isNegative ? `-${formattedValue}` : formattedValue;

        return {value: formattedValue};
    };

    /**
     * @inheritDoc
     */
    onFocus = (state: InputState<number>): InputState<number> => state;

    /**
     * @inheritDoc
     */
    onChange = (before: InputState<number>, after: string): InputState<number> => {
        let cleanedValue = after;

        // Replace "." with ","
        cleanedValue = cleanedValue.replace(/\./g, ',');

        // Ensure valid characters: digits, comma, minus
        if (/[^\d,-]/.test(cleanedValue)) {
            return before;
        }

        // Ensure only one comma
        if ((cleanedValue.match(/,/g) || []).length > 1) {
            return before;
        }

        // Ensure minus is at the beginning
        if (cleanedValue.slice(1).indexOf('-') !== -1) {
            return before;
        }

        // Handle leading zeros and ensure correct decimal formatting
        if (/^-?00/.test(cleanedValue) || /^-?0\d/.test(cleanedValue)) {
            cleanedValue = cleanedValue.replace(/^(-?)0/, '$1');
        }

        // Limit decimal places to 2
        const decimalSeparatorIndex = cleanedValue.indexOf(',');
        if (decimalSeparatorIndex >= 0 && cleanedValue.length - decimalSeparatorIndex > 3) {
            return before;
        }

        // Insert "0" before ","
        if (/^-?,/.test(cleanedValue)) {
            cleanedValue = cleanedValue.replace(/^(-?),/, '$10,');
        }

        return {value: cleanedValue};
    };

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState<number>): number | null => {
        let normalizedValue: string = state.value;

        normalizedValue = normalizedValue.replace(',', '.');

        if (normalizedValue === '' || normalizedValue === '-') {
            return null;
        }

        const floatValue = parseFloat(normalizedValue);
        return Math.round(floatValue * 100);
    };
}

export default CentInputFormatter;
