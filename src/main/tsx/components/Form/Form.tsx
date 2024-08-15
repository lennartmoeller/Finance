import React, {ReactNode, useRef} from 'react';

import FormContext from "@/components/Form/context/FormContext";
import {Nullable} from "@/utils/types";

interface FormProps<V extends object> {
    initial: Nullable<V>;
    onChange: (item: Nullable<V>) => Nullable<V>;
    children: ReactNode;
}

const TableRow = <V extends object>({initial, onChange, children}: FormProps<V>) => {
    const item = useRef(initial);

    const setters = useRef(new Map<keyof V, (value: V[keyof V] | null) => void>());

    const registerValueSetter = <K extends keyof V>(property: K, setValue: (value: V[K] | null) => void): void => {
        if (!setters.current.has(property)) {
            setters.current.set(property, setValue as (value: V[keyof V] | null) => void);
            setValue(initial[property]);
        }
    };

    const updatePropertyValue = <K extends keyof V>(changedProperty: K, changedValue: V[K] | null): boolean => {
        if (item.current[changedProperty] === changedValue) {
            return false; // do nothing: value hasn't changed
        }
        const updatedItem: Nullable<V> = onChange({...item.current, [changedProperty]: changedValue});
        for (const property in updatedItem) {
            if (item.current[property] === updatedItem[property]) {
                continue; // do nothing: value hasn't changed
            }
            const setter = setters.current.get(property);
            if (setter) {
                setter(updatedItem[property]);
            }
        }
        item.current = updatedItem;
        return true;
    };

    return (
        <FormContext.Provider value={{registerValueSetter, updatePropertyValue, initialValue: initial}}>
            {children}
        </FormContext.Provider>
    );
};

export default TableRow;
