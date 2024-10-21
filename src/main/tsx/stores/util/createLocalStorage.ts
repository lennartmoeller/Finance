import {StorageValue} from "zustand/middleware";

const createLocalStorage = <T>(serialize: (state: T) => string, deserialize: (stringValue: string) => T) => ({
    getItem: (name: string): StorageValue<T> | null => {
        const stringValue: string | null = localStorage.getItem(name);
        if (!stringValue) {
            return null;
        }
        return {
            state: deserialize(stringValue),
        };
    },
    setItem: (name: string, value: StorageValue<T>): void => {
        const stringValue: string = serialize(value.state);
        localStorage.setItem(name, stringValue);
    },
    removeItem: (name: string): void => {
        localStorage.removeItem(name);
    }
});

export default createLocalStorage;
