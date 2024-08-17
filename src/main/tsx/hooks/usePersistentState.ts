import {useState} from "react";

const usePersistentState = <T, >(key: string, defaultValue: T): [T, (value: T) => void] => {
    const [storedValue, setStoredValue] = useState<T>(() => {
        const item: string | null = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    });

    const setValue = (value: T) => {
        setStoredValue(value);
        localStorage.setItem(key, JSON.stringify(value));
    };

    return [storedValue, setValue];
};

export default usePersistentState;
