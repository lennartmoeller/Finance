import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";

class CentInputFormatter extends InputFormatter<number> {
    public valueToString(cents: number | null): string {
        if (cents === null) {
            return "";
        }

        const isNegative = cents < 0;
        const absoluteCents = Math.abs(cents);

        const formattedValue = (absoluteCents / 100).toFixed(2).replace(".", ",");
        return isNegative ? `-${formattedValue}` : formattedValue;
    }

    public stringToValue(string: string): number | null {
        const normalizedValue = string.replace(",", ".");

        if (normalizedValue === "" || normalizedValue === "-") {
            return null;
        }

        const floatValue = Number.parseFloat(normalizedValue);
        return Math.round(floatValue * 100);
    }

    public onChange(before: InputState<number>, after: string): InputState<number> {
        let cleanedValue = after;

        // Replace "." with ","
        cleanedValue = cleanedValue.replaceAll(".", ",");

        // Ensure valid characters: digits, comma, minus
        if (/[^\d,-]/.test(cleanedValue)) {
            return before;
        }

        // Ensure only one comma
        if ((cleanedValue.match(/,/g) || []).length > 1) {
            return before;
        }

        // Ensure minus is at the beginning
        if (cleanedValue.slice(1).includes("-")) {
            return before;
        }

        // Handle leading zeros and ensure correct decimal formatting
        if (/^-?00/.test(cleanedValue) || /^-?0\d/.test(cleanedValue)) {
            cleanedValue = cleanedValue.replace(/^(-?)0/, "$1");
        }

        // Limit decimal places to 2
        const decimalSeparatorIndex = cleanedValue.indexOf(",");
        if (decimalSeparatorIndex >= 0 && cleanedValue.length - decimalSeparatorIndex > 3) {
            return before;
        }

        // Insert "0" before ","
        if (/^-?,/.test(cleanedValue)) {
            cleanedValue = cleanedValue.replace(/^(-?),/, "$1" + "0,");
        }

        return super.onChange(before, cleanedValue);
    }
}

export default CentInputFormatter;
