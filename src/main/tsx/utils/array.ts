export const filterDuplicates = <T>(array: Array<T>): Array<T> => {
    return array.filter((value, index, self) => self.indexOf(value) === index);
};
