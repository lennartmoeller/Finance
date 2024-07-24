import React, {useEffect, useState} from "react";

function usePersistentState<T>(key: string, initialValue: T): [T, React.Dispatch<React.SetStateAction<T>>] {
    const getStoredValue = (): T => {
        const storedValue: string | null = localStorage.getItem(key);
        return storedValue ? JSON.parse(storedValue) : initialValue;
    };

    const [value, setValue] = useState<T>(getStoredValue);

    useEffect(() => {
        localStorage.setItem(key, JSON.stringify(value));
    }, [key, value]);

    return [value, setValue];
}

export default usePersistentState;
