import {StorageValue} from "zustand/middleware";

const createStorage = <A, B>(serialize: (state: A) => B, deserialize: (state: B) => A) => ({
    getItem: (name: string): StorageValue<A> | null => {
        const stringValue: string | null = localStorage.getItem(name);
        if (!stringValue) {
            return null;
        }
        const storageValue: StorageValue<B> = JSON.parse(stringValue);
        return {
            ...storageValue,
            state: deserialize(storageValue.state),
        };
    },
    setItem: (name: string, value: StorageValue<A>): void => {
        const storageValue: StorageValue<B> = {
            ...value,
            state: serialize(value.state),
        };
        const stringValue: string = JSON.stringify(storageValue);
        localStorage.setItem(name, stringValue);
    },
    removeItem: (name: string): void => {
        localStorage.removeItem(name);
    }
});

export default createStorage;
