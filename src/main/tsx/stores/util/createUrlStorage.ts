import {StorageValue} from "zustand/middleware";

const createUrlStorage = <T>(serialize: (state: T) => string, deserialize: (stringValue: string) => T) => ({
    getItem: (name: string): StorageValue<T> | null => {
        const url = new URL(window.location.href);
        const stringValue: string | null = url.searchParams.get(name);
        if (!stringValue) {
            return null;
        }
        return {
            state: deserialize(stringValue),
        };
    },
    setItem: (name: string, value: StorageValue<T>): void => {
        const stringValue: string = serialize(value.state);
        const url = new URL(window.location.href);
        url.searchParams.set(name, stringValue);
        window.history.replaceState(null, '', url.toString());
    },
    removeItem: (name: string): void => {
        const url = new URL(window.location.href);
        url.searchParams.delete(name);
        window.history.replaceState(null, '', url.toString());
    }
});

export default createUrlStorage;
