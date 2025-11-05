export const filterDuplicates = <T>(array: Array<T>): Array<T> => {
    return array.filter((value, index, self) => self.indexOf(value) === index);
};

export const ensureArray = <T>(value: T | T[] | null | undefined): T[] => {
    if (value === null || value === undefined) {
        return [];
    }
    return Array.isArray(value) ? value : [value];
};
