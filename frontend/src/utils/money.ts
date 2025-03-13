export const getEuroString = (cents: number, stringLength?: number): string => {
    const euroString: string = new Intl.NumberFormat('de-DE', {style: 'currency', currency: 'EUR'}).format(cents / 100);

    if (stringLength === undefined) {
        return euroString;
    }

    const currentLength: number = euroString.length;
    const spacesNeeded: number = stringLength - currentLength;
    const spaces: string = '\u00A0'.repeat(spacesNeeded);

    if (euroString.startsWith('-')) {
        return "-" + spaces + euroString.slice(1);
    } else {
        return spaces + euroString;
    }
};
