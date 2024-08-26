import {useState} from 'react';

type ConversionFunctions<T> = {
    serialize: (value: T) => string;
    deserialize: (value: string) => T;
};

const usePersistentState = <T>(
    key: string,
    defaultValue: T,
    conversions?: ConversionFunctions<T>,
): [T, (value: T) => void] => {
    const serialize = conversions?.serialize ?? JSON.stringify;
    const deserialize = conversions?.deserialize ?? JSON.parse;

    const [storedValue, setStoredValue] = useState<T>(() => {
        const item: string | null = localStorage.getItem(key);
        return item ? deserialize(item) : defaultValue;
    });

    const setValue = (value: T) => {
        setStoredValue(value);
        localStorage.setItem(key, serialize(value));
    };

    return [storedValue, setValue];
};

export default usePersistentState;
